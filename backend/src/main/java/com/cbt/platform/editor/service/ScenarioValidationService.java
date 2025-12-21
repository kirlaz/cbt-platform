package com.cbt.platform.editor.service;

import com.cbt.platform.editor.dto.ValidationResultResponse;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Service interface for scenario validation operations
 */
public interface ScenarioValidationService {

    /**
     * Validate scenario JSON structure
     *
     * @param scenarioJson scenario JSON to validate
     * @return validation result with errors if any
     */
    ValidationResultResponse validate(JsonNode scenarioJson);

    /**
     * Check if scenario JSON is valid
     *
     * @param scenarioJson scenario JSON to check
     * @return true if valid, false otherwise
     */
    boolean isValid(JsonNode scenarioJson);
}
