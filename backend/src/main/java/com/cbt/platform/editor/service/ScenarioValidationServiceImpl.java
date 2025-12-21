package com.cbt.platform.editor.service;

import com.cbt.platform.editor.dto.ValidationResultResponse;
import com.cbt.platform.editor.dto.ValidationResultResponse.ValidationError;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ScenarioValidationService
 * Validates scenario JSON structure and content
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioValidationServiceImpl implements ScenarioValidationService {

    @Override
    public ValidationResultResponse validate(JsonNode scenarioJson) {
        log.debug("Validating scenario JSON");
        List<ValidationError> errors = new ArrayList<>();

        if (scenarioJson == null || scenarioJson.isNull()) {
            errors.add(new ValidationError("REQUIRED_FIELD", "Scenario JSON is required", "scenarioJson", "ERROR"));
            return new ValidationResultResponse(false, errors, null);
        }

        validateMetadata(scenarioJson, errors);
        validateSessions(scenarioJson, errors);

        boolean isValid = errors.isEmpty();
        log.debug("Validation complete: valid={}, errors={}", isValid, errors.size());

        return new ValidationResultResponse(isValid, errors, null);
    }

    @Override
    public boolean isValid(JsonNode scenarioJson) {
        return validate(scenarioJson).isValid();
    }

    private void validateMetadata(JsonNode scenarioJson, List<ValidationError> errors) {
        if (!scenarioJson.has("meta")) {
            errors.add(new ValidationError("REQUIRED_FIELD", "Meta field is required", "meta", "ERROR"));
            return;
        }

        JsonNode meta = scenarioJson.get("meta");
        if (!meta.has("title")) {
            errors.add(new ValidationError("REQUIRED_FIELD", "Title is required in meta", "meta.title", "ERROR"));
        }
        if (!meta.has("description")) {
            errors.add(new ValidationError("REQUIRED_FIELD", "Description is required in meta", "meta.description", "WARNING"));
        }
    }

    private void validateSessions(JsonNode scenarioJson, List<ValidationError> errors) {
        if (!scenarioJson.has("sessions")) {
            errors.add(new ValidationError("REQUIRED_FIELD", "Sessions field is required", "sessions", "ERROR"));
            return;
        }

        JsonNode sessions = scenarioJson.get("sessions");
        if (!sessions.isArray()) {
            errors.add(new ValidationError("INVALID_TYPE", "Sessions must be an array", "sessions", "ERROR"));
            return;
        }

        if (sessions.size() == 0) {
            errors.add(new ValidationError("EMPTY_ARRAY", "At least one session is required", "sessions", "ERROR"));
            return;
        }

        for (int i = 0; i < sessions.size(); i++) {
            validateSession(sessions.get(i), i, errors);
        }
    }

    private void validateSession(JsonNode session, int index, List<ValidationError> errors) {
        String prefix = "sessions[" + index + "]";

        if (!session.has("id")) {
            errors.add(new ValidationError("REQUIRED_FIELD", "Session ID is required", prefix + ".id", "ERROR"));
        }
        if (!session.has("name")) {
            errors.add(new ValidationError("REQUIRED_FIELD", "Session name is required", prefix + ".name", "ERROR"));
        }
        if (!session.has("blocks")) {
            errors.add(new ValidationError("REQUIRED_FIELD", "Blocks are required", prefix + ".blocks", "ERROR"));
            return;
        }

        JsonNode blocks = session.get("blocks");
        if (!blocks.isArray()) {
            errors.add(new ValidationError("INVALID_TYPE", "Blocks must be an array", prefix + ".blocks", "ERROR"));
            return;
        }

        if (blocks.size() == 0) {
            errors.add(new ValidationError("EMPTY_ARRAY", "At least one block is required", prefix + ".blocks", "ERROR"));
        }

        for (int i = 0; i < blocks.size(); i++) {
            validateBlock(blocks.get(i), index, i, errors);
        }
    }

    private void validateBlock(JsonNode block, int sessionIndex, int blockIndex, List<ValidationError> errors) {
        String prefix = "sessions[" + sessionIndex + "].blocks[" + blockIndex + "]";

        if (!block.has("id")) {
            errors.add(new ValidationError("REQUIRED_FIELD", "Block ID is required", prefix + ".id", "ERROR"));
        }
        if (!block.has("type")) {
            errors.add(new ValidationError("REQUIRED_FIELD", "Block type is required", prefix + ".type", "ERROR"));
        }
    }
}
