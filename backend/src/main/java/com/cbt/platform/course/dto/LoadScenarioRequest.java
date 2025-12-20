package com.cbt.platform.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request DTO for loading a course from scenario file
 */
public record LoadScenarioRequest(
        @NotBlank(message = "Scenario path is required")
        @Pattern(
                regexp = "^scenarios/[a-z0-9-]+/scenario_\\d+\\.\\d+\\.\\d+\\.json$",
                message = "Scenario path must match pattern: scenarios/{slug}/scenario_{version}.json"
        )
        String scenarioPath
) {
}
