package com.cbt.platform.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating user profile
 */
public record UpdateProfileRequest(
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone must be a valid international format")
        String phone,

        @Size(max = 50, message = "Timezone must not exceed 50 characters")
        String timezone,

        @Pattern(regexp = "^(ru|en)$", message = "Language must be 'ru' or 'en'")
        String preferredLanguage
) {
}
