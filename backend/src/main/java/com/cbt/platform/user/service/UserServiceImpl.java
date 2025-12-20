package com.cbt.platform.user.service;

import com.cbt.platform.security.JwtTokenProvider;
import com.cbt.platform.user.dto.*;
import com.cbt.platform.user.entity.User;
import com.cbt.platform.user.exception.InvalidCredentialsException;
import com.cbt.platform.user.exception.UserAlreadyExistsException;
import com.cbt.platform.user.exception.UserNotFoundException;
import com.cbt.platform.user.mapper.UserMapper;
import com.cbt.platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of UserService for user management operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.email());

        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new UserAlreadyExistsException(request.email());
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        // Set defaults if not provided
        if (request.timezone() != null) {
            user.setTimezone(request.timezone());
        }
        if (request.preferredLanguage() != null) {
            user.setPreferredLanguage(request.preferredLanguage());
        }

        user = userRepository.save(user);
        log.info("User registered successfully: id={}, email={}", user.getId(), user.getEmail());

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.debug("Login attempt: {}", request.email());

        User user = userRepository.findActiveByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        // Update last login timestamp
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("User logged in: id={}, email={}", user.getId(), user.getEmail());

        // Generate JWT tokens
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getEmail(),
                List.of("ROLE_" + user.getRole().name())
        );
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        return new AuthResponse(
                accessToken,
                refreshToken,
                userMapper.toResponse(user)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        log.debug("Finding user by id: {}", id);

        return userRepository.findActiveById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findByEmail(String email) {
        log.debug("Finding user by email: {}", email);

        return userRepository.findActiveByEmail(email)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(Pageable pageable) {
        log.debug("Fetching all users: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());

        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UUID id, UpdateProfileRequest request) {
        log.info("Updating user profile: id={}", id);

        User user = userRepository.findActiveById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userMapper.updateEntity(user, request);
        user = userRepository.save(user);

        log.info("User profile updated: id={}", user.getId());
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void updateLastLogin(UUID id) {
        log.debug("Updating last login: id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        log.info("Soft deleting user: id={}", id);

        User user = userRepository.findActiveById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.softDelete(id);
        log.info("User soft deleted: id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }
}
