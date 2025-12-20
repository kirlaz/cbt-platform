package com.cbt.platform.progress.service;

import com.cbt.platform.course.exception.CourseNotFoundException;
import com.cbt.platform.course.repository.CourseRepository;
import com.cbt.platform.progress.dto.*;
import com.cbt.platform.progress.entity.UserProgress;
import com.cbt.platform.progress.exception.CourseAlreadyStartedException;
import com.cbt.platform.progress.exception.ProgressNotFoundException;
import com.cbt.platform.progress.mapper.ProgressMapper;
import com.cbt.platform.progress.repository.ProgressRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of ProgressService for user progress management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressServiceImpl implements ProgressService {

    private final ProgressRepository progressRepository;
    private final CourseRepository courseRepository;
    private final ProgressMapper progressMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public ProgressResponse startCourse(UUID userId, StartCourseRequest request) {
        log.info("Starting course for user: userId={}, courseId={}", userId, request.courseId());

        // Verify course exists
        if (!courseRepository.existsById(request.courseId())) {
            throw new CourseNotFoundException(request.courseId());
        }

        // Check if already started
        if (progressRepository.existsByUserIdAndCourseId(userId, request.courseId())) {
            throw new CourseAlreadyStartedException(userId, request.courseId());
        }

        // Create initial progress with empty userData
        ObjectNode emptyUserData = objectMapper.createObjectNode();

        UserProgress progress = UserProgress.builder()
                .userId(userId)
                .courseId(request.courseId())
                .userData(emptyUserData)
                .completedSessions(new ArrayList<>())
                .completedBlocks(new ArrayList<>())
                .completionPercentage(0)
                .isCompleted(false)
                .startedAt(LocalDateTime.now())
                .lastActivityAt(LocalDateTime.now())
                .build();

        progress = progressRepository.save(progress);
        log.info("Course started: progressId={}, userId={}, courseId={}",
                progress.getId(), userId, request.courseId());

        return progressMapper.toResponse(progress);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgressResponse getProgress(UUID userId, UUID courseId) {
        log.debug("Fetching progress: userId={}, courseId={}", userId, courseId);

        return progressRepository.findByUserIdAndCourseId(userId, courseId)
                .map(progressMapper::toResponse)
                .orElseThrow(() -> new ProgressNotFoundException(userId, courseId));
    }

    @Override
    @Transactional(readOnly = true)
    public ProgressResponse getProgressById(UUID progressId) {
        log.debug("Fetching progress by id: {}", progressId);

        return progressRepository.findById(progressId)
                .map(progressMapper::toResponse)
                .orElseThrow(() -> new ProgressNotFoundException(progressId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgressResponse> getAllProgressForUser(UUID userId) {
        log.debug("Fetching all progress for user: {}", userId);

        return progressRepository.findByUserId(userId)
                .stream()
                .map(progressMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgressResponse> getActiveProgressForUser(UUID userId) {
        log.debug("Fetching active progress for user: {}", userId);

        return progressRepository.findActiveProgressByUserId(userId)
                .stream()
                .map(progressMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProgressResponse updateProgress(UUID userId, UUID courseId, UpdateProgressRequest request) {
        log.info("Updating progress: userId={}, courseId={}", userId, courseId);

        UserProgress progress = progressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ProgressNotFoundException(userId, courseId));

        progressMapper.updateEntity(progress, request);
        progress.setLastActivityAt(LocalDateTime.now());

        progress = progressRepository.save(progress);
        log.info("Progress updated: progressId={}", progress.getId());

        return progressMapper.toResponse(progress);
    }

    @Override
    @Transactional
    public ProgressResponse updateUserData(UUID userId, UUID courseId, UpdateUserDataRequest request) {
        log.info("Updating user data: userId={}, courseId={}, merge={}", userId, courseId, request.merge());

        UserProgress progress = progressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ProgressNotFoundException(userId, courseId));

        JsonNode newUserData;
        if (request.merge() && progress.getUserData() != null) {
            // Merge: combine existing userData with new data
            newUserData = mergeJsonNodes(progress.getUserData(), request.userData());
        } else {
            // Replace: use new data completely
            newUserData = request.userData();
        }

        progress.setUserData(newUserData);
        progress.setLastActivityAt(LocalDateTime.now());

        progress = progressRepository.save(progress);
        log.info("User data updated: progressId={}", progress.getId());

        return progressMapper.toResponse(progress);
    }

    @Override
    @Transactional
    public ProgressResponse completeSession(UUID userId, UUID courseId, CompleteSessionRequest request) {
        log.info("Completing session: userId={}, courseId={}, sessionId={}",
                userId, courseId, request.sessionId());

        UserProgress progress = progressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ProgressNotFoundException(userId, courseId));

        // Add session to completed list if not already there
        if (!progress.getCompletedSessions().contains(request.sessionId())) {
            progress.getCompletedSessions().add(request.sessionId());
        }

        progress.setLastActivityAt(LocalDateTime.now());
        progress = progressRepository.save(progress);

        log.info("Session completed: progressId={}, sessionId={}", progress.getId(), request.sessionId());
        return progressMapper.toResponse(progress);
    }

    @Override
    @Transactional
    public ProgressResponse completeBlock(UUID userId, UUID courseId, String sessionId, String blockId) {
        log.info("Completing block: userId={}, courseId={}, sessionId={}, blockId={}",
                userId, courseId, sessionId, blockId);

        UserProgress progress = progressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ProgressNotFoundException(userId, courseId));

        String blockKey = sessionId + ":" + blockId;

        // Add block to completed list if not already there
        if (!progress.getCompletedBlocks().contains(blockKey)) {
            progress.getCompletedBlocks().add(blockKey);
        }

        progress.setLastActivityAt(LocalDateTime.now());
        progress = progressRepository.save(progress);

        log.info("Block completed: progressId={}, blockKey={}", progress.getId(), blockKey);
        return progressMapper.toResponse(progress);
    }

    @Override
    @Transactional
    public ProgressResponse completeCourse(UUID userId, UUID courseId) {
        log.info("Completing course: userId={}, courseId={}", userId, courseId);

        UserProgress progress = progressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ProgressNotFoundException(userId, courseId));

        progress.setIsCompleted(true);
        progress.setCompletionPercentage(100);
        progress.setCompletedAt(LocalDateTime.now());
        progress.setLastActivityAt(LocalDateTime.now());

        progress = progressRepository.save(progress);
        log.info("Course completed: progressId={}", progress.getId());

        return progressMapper.toResponse(progress);
    }

    @Override
    @Transactional
    public void deleteProgress(UUID userId, UUID courseId) {
        log.info("Deleting progress: userId={}, courseId={}", userId, courseId);

        UserProgress progress = progressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ProgressNotFoundException(userId, courseId));

        progressRepository.delete(progress);
        log.info("Progress deleted: progressId={}", progress.getId());
    }

    /**
     * Merge two JSON nodes (deep merge)
     * Fields in newData override fields in existingData
     */
    private JsonNode mergeJsonNodes(JsonNode existingData, JsonNode newData) {
        if (existingData == null || existingData.isNull()) {
            return newData;
        }
        if (newData == null || newData.isNull()) {
            return existingData;
        }

        ObjectNode merged = existingData.deepCopy();
        newData.fields().forEachRemaining(entry -> {
            merged.set(entry.getKey(), entry.getValue());
        });

        return merged;
    }
}
