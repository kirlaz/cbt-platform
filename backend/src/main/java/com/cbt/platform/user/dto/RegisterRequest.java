package com.cbt.platform.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for user registration
 */
public record RegisterRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit"
        )
        String password,

        @NotBlank(message = "Name is required")
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
