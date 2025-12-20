package com.cbt.platform.user.service;

import com.cbt.platform.user.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for user management operations
 */
public interface UserService {

    /**
     * Register a new user
     *
     * @param request registration data
     * @return created user response
     */
    UserResponse register(RegisterRequest request);

    /**
     * Authenticate user and return auth tokens
     *
     * @param request login credentials
     * @return authentication response with tokens
     */
    AuthResponse login(LoginRequest request);

    /**
     * Find user by ID
     *
     * @param id user ID
     * @return user response
     */
    UserResponse findById(UUID id);

    /**
     * Find user by email
     *
     * @param email user email
     * @return user response
     */
    UserResponse findByEmail(String email);

    /**
     * Get all active users (paginated)
     *
     * @param pageable pagination parameters
     * @return page of user responses
     */
    Page<UserResponse> findAll(Pageable pageable);

    /**
     * Update user profile
     *
     * @param id      user ID
     * @param request update data
     * @return updated user response
     */
    UserResponse updateProfile(UUID id, UpdateProfileRequest request);

    /**
     * Update last login timestamp
     *
     * @param id user ID
     */
    void updateLastLogin(UUID id);

    /**
     * Soft delete user
     *
     * @param id user ID
     */
    void deleteUser(UUID id);

    /**
     * Check if user exists by email
     *
     * @param email user email
     * @return true if exists
     */
    boolean existsByEmail(String email);
}
