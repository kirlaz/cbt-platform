package com.cbt.platform.user.dto;

/**
 * Response DTO for authentication (login/register)
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserResponse user
) {
}
