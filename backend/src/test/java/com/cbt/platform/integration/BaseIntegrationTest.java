package com.cbt.platform.integration;

import com.cbt.platform.security.JwtTokenProvider;
import com.cbt.platform.user.entity.User;
import com.cbt.platform.user.entity.UserRole;
import com.cbt.platform.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

/**
 * Base class for integration tests
 * Provides common utilities for testing with real database and security
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    /**
     * Create a test user in the database
     */
    protected User createTestUser(String email, String password, UserRole role) {
        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .name("Test User")
                .role(role)
                .isActive(true)
                .timezone("UTC")
                .preferredLanguage("en")
                .build();
        return userRepository.save(user);
    }

    /**
     * Create a regular test user
     */
    protected User createTestUser() {
        return createTestUser("test@example.com", "password123", UserRole.USER);
    }

    /**
     * Create an admin test user
     */
    protected User createAdminUser() {
        return createTestUser("admin@example.com", "password123", UserRole.ADMIN);
    }

    /**
     * Generate JWT access token for a user
     */
    protected String generateAccessToken(User user) {
        return jwtTokenProvider.generateAccessToken(
                user.getEmail(),
                List.of("ROLE_" + user.getRole().name())
        );
    }

    /**
     * Create HttpEntity with JWT authentication header
     */
    protected <T> HttpEntity<T> withAuth(T body, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return new HttpEntity<>(body, headers);
    }

    /**
     * Create HttpEntity with JWT authentication header (no body)
     */
    protected HttpEntity<Void> withAuth(String token) {
        return withAuth(null, token);
    }

    /**
     * Create HttpEntity without authentication
     */
    protected <T> HttpEntity<T> withoutAuth(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}
