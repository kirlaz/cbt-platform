package com.cbt.platform.engine.service;

import com.cbt.platform.course.entity.Course;
import com.cbt.platform.course.exception.CourseNotFoundException;
import com.cbt.platform.course.repository.CourseRepository;
import com.cbt.platform.engine.dto.BlockInputRequest;
import com.cbt.platform.engine.dto.BlockResult;
import com.cbt.platform.engine.dto.BlockType;
import com.cbt.platform.engine.exception.BlockNotFoundException;
import com.cbt.platform.engine.exception.SessionNotFoundException;
import com.cbt.platform.engine.handler.BlockHandler;
import com.cbt.platform.progress.entity.UserProgress;
import com.cbt.platform.progress.exception.ProgressNotFoundException;
import com.cbt.platform.progress.repository.UserProgressRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Core course engine service
 * Orchestrates block processing, manages state transitions, handles navigation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseEngine {

    private final CourseRepository courseRepository;
    private final UserProgressRepository progressRepository;
    private final BlockHandlerRegistry handlerRegistry;
    private final ObjectMapper objectMapper;

    /**
     * Get current block for user (from UserProgress state)
     *
     * @param userId   User ID
     * @param courseId Course ID
     * @return BlockResult for current block
     */
    @Transactional(readOnly = true)
    public BlockResult getCurrentBlock(UUID userId, UUID courseId) {
        log.debug("Getting current block for user: {} in course: {}", userId, courseId);

        UserProgress progress = progressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ProgressNotFoundException(userId, courseId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        JsonNode scenarioJson = course.getScenarioJson();
        String currentSessionId = progress.getCurrentSessionId();
        Integer currentBlockIndex = progress.getCurrentBlockIndex();

        if (currentSessionId == null) {
            // No session started yet - start first session
            currentSessionId = getFirstSessionId(scenarioJson);
            currentBlockIndex = 0;
        }

        JsonNode currentBlock = getBlockByIndex(scenarioJson, currentSessionId, currentBlockIndex);
        BlockType blockType = BlockType.valueOf(currentBlock.get("type").asText().toUpperCase());

        BlockHandler handler = handlerRegistry.getHandler(blockType);
        return handler.handle(currentBlock, progress.getUserData(), null);
    }

    /**
     * Process user input for current block
     *
     * @param userId  User ID
     * @param courseId Course ID
     * @param request User input request
     * @return BlockResult after processing input
     */
    @Transactional
    public BlockResult processBlockInput(UUID userId, UUID courseId, BlockInputRequest request) {
        log.debug("Processing block input for user: {} in course: {}, block: {}",
                userId, courseId, request.blockId());

        UserProgress progress = progressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ProgressNotFoundException(userId, courseId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        JsonNode scenarioJson = course.getScenarioJson();
        String currentSessionId = progress.getCurrentSessionId();
        Integer currentBlockIndex = progress.getCurrentBlockIndex();

        JsonNode currentBlock = getBlockByIndex(scenarioJson, currentSessionId, currentBlockIndex);
        String blockId = currentBlock.get("id").asText();

        // Verify user is submitting input for current block
        if (!blockId.equals(request.blockId())) {
            throw new IllegalStateException("Cannot submit input for block " + request.blockId() +
                    " - current block is " + blockId);
        }

        BlockType blockType = BlockType.valueOf(currentBlock.get("type").asText().toUpperCase());
        BlockHandler handler = handlerRegistry.getHandler(blockType);

        // Process block with user input
        BlockResult result = handler.handle(currentBlock, progress.getUserData(), request.input());

        // If block is complete, update progress
        if (result.isComplete()) {
            // Update userData
            if (result.getUpdatedUserData() != null) {
                progress.setUserData(result.getUpdatedUserData());
            }

            // Move to next block
            progress.setCurrentBlockIndex(currentBlockIndex + 1);

            // Check if session is complete
            JsonNode session = getSession(scenarioJson, currentSessionId);
            JsonNode blocks = session.get("blocks");
            if (progress.getCurrentBlockIndex() >= blocks.size()) {
                // Session complete
                progress.setCurrentBlockIndex(0);

                // Move to next session if available
                String nextSessionId = session.has("next_session") ?
                        session.get("next_session").asText() : null;
                progress.setCurrentSessionId(nextSessionId);
            }

            progressRepository.save(progress);
            log.debug("Progress updated: sessionId={}, blockIndex={}",
                    progress.getCurrentSessionId(), progress.getCurrentBlockIndex());
        }

        return result;
    }

    /**
     * Navigate to next block
     */
    @Transactional
    public BlockResult nextBlock(UUID userId, UUID courseId) {
        log.debug("Navigating to next block for user: {} in course: {}", userId, courseId);

        UserProgress progress = progressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ProgressNotFoundException(userId, courseId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        // Increment block index
        progress.setCurrentBlockIndex(progress.getCurrentBlockIndex() + 1);
        progressRepository.save(progress);

        return getCurrentBlock(userId, courseId);
    }

    /**
     * Get first session ID from scenario
     */
    private String getFirstSessionId(JsonNode scenarioJson) {
        JsonNode meta = scenarioJson.get("meta");
        if (meta != null && meta.has("sessions") && meta.get("sessions").isArray()) {
            return meta.get("sessions").get(0).asText();
        }
        throw new SessionNotFoundException("No sessions defined in scenario");
    }

    /**
     * Get session by ID
     */
    private JsonNode getSession(JsonNode scenarioJson, String sessionId) {
        JsonNode sessions = scenarioJson.get("sessions");
        if (sessions == null || !sessions.has(sessionId)) {
            throw new SessionNotFoundException(sessionId);
        }
        return sessions.get(sessionId);
    }

    /**
     * Get block by index in session
     */
    private JsonNode getBlockByIndex(JsonNode scenarioJson, String sessionId, int blockIndex) {
        JsonNode session = getSession(scenarioJson, sessionId);
        JsonNode blocks = session.get("blocks");

        if (blocks == null || !blocks.isArray() || blockIndex >= blocks.size()) {
            throw new BlockNotFoundException(sessionId, "block at index " + blockIndex);
        }

        return blocks.get(blockIndex);
    }
}
