package com.cbt.platform.progress.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for updating user data (merge or replace)
 */
public record UpdateUserDataRequest(
        @NotNull(message = "User data is required")
        JsonNode userData,

        /**
         * If true, merges with existing userData
         * If false, replaces userData completely
         */
        boolean merge
) {
    public UpdateUserDataRequest {
        // Default to merge if not specified
        if (userData == null) {
            merge = true;
        }
    }
}
