package com.cbt.platform.user.dto;

import com.cbt.platform.user.entity.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for user data
 */
public record UserResponse(
        UUID id,
        String email,
        String name,
        String phone,
        String timezone,
        String preferredLanguage,
        UserRole role,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime lastLoginAt
) {
}
