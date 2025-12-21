package com.cbt.platform.e2e;

import com.cbt.platform.integration.BaseIntegrationTest;
import com.cbt.platform.user.dto.AuthResponse;
import com.cbt.platform.user.dto.LoginRequest;
import com.cbt.platform.user.dto.RegisterRequest;
import com.cbt.platform.user.dto.UserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.POST;

/**
 * E2E tests for complete authentication flow
 * Tests the entire user journey from registration to login
 */
@DisplayName("Authentication Flow E2E Tests")
class AuthFlowTest extends BaseIntegrationTest {

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    // ==================== Complete Registration → Login Flow ====================

    @Test
    @DisplayName("Complete flow: Register → Login → Access protected resource")
    void shouldCompleteFullAuthenticationFlow() {
        String email = "newuser@example.com";
        String password = "SecurePassword123!";

        // Step 1: Register new user
        RegisterRequest registerRequest = new RegisterRequest(
                email,
                password,
                "John Doe",
                null,  // phone
                "UTC",
                "en"
        );

        ResponseEntity<UserResponse> registerResponse = restTemplate.exchange(
                "/api/auth/register",
                POST,
                withoutAuth(registerRequest),
                UserResponse.class
        );

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(registerResponse.getBody()).isNotNull();
        assertThat(registerResponse.getBody().email()).isEqualTo(email);
        assertThat(registerResponse.getBody().name()).isEqualTo("John Doe");
        assertThat(registerResponse.getHeaders().getLocation()).isNotNull();

        // Step 2: Login with registered credentials
        LoginRequest loginRequest = new LoginRequest(email, password);

        ResponseEntity<AuthResponse> loginResponse = restTemplate.exchange(
                "/api/auth/login",
                POST,
                withoutAuth(loginRequest),
                AuthResponse.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        assertThat(loginResponse.getBody().accessToken()).isNotBlank();
        assertThat(loginResponse.getBody().refreshToken()).isNotBlank();
        assertThat(loginResponse.getBody().user().email()).isEqualTo(email);

        // Step 3: Use access token to get user profile
        String accessToken = loginResponse.getBody().accessToken();
        String userId = loginResponse.getBody().user().id().toString();

        ResponseEntity<UserResponse> profileResponse = restTemplate.exchange(
                "/api/users/" + userId,
                org.springframework.http.HttpMethod.GET,
                withAuth(accessToken),
                UserResponse.class
        );

        assertThat(profileResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(profileResponse.getBody()).isNotNull();
        assertThat(profileResponse.getBody().email()).isEqualTo(email);

        // Verify user exists in database
        assertThat(userRepository.findByEmailIgnoreCase(email)).isPresent();
    }

    // ==================== Registration Tests ====================

    @Test
    @DisplayName("Should register user with minimal required fields")
    void shouldRegisterUserWithMinimalFields() {
        // Given
        RegisterRequest request = new RegisterRequest(
                "minimal@example.com",
                "Password123!",
                "Minimal User",
                null,  // phone
                null,  // timezone optional
                null   // language optional
        );

        // When
        ResponseEntity<UserResponse> response = restTemplate.exchange(
                "/api/auth/register",
                POST,
                withoutAuth(request),
                UserResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().email()).isEqualTo("minimal@example.com");
        assertThat(response.getBody().timezone()).isNotNull(); // Should have default
        assertThat(response.getBody().preferredLanguage()).isNotNull(); // Should have default
    }

    @Test
    @DisplayName("Should return 409 when registering with existing email")
    void shouldReturn409WhenEmailAlreadyExists() {
        // Given - existing user
        String email = "existing@example.com";
        createTestUser(email, "password", com.cbt.platform.user.entity.UserRole.USER);

        // When - try to register with same email
        RegisterRequest request = new RegisterRequest(
                email,
                "AnotherPassword123!",
                "Another User",
                null,  // phone
                null,  // timezone
                null   // language
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/auth/register",
                POST,
                withoutAuth(request),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("Should return 400 when registration data is invalid")
    void shouldReturn400WhenRegistrationDataInvalid() {
        // Given - invalid email
        RegisterRequest request = new RegisterRequest(
                "not-an-email",
                "password",
                "User",
                null,  // phone
                null,  // timezone
                null   // language
        );

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/auth/register",
                POST,
                withoutAuth(request),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // ==================== Login Tests ====================

    @Test
    @DisplayName("Should login successfully with correct credentials")
    void shouldLoginSuccessfully() {
        // Given - existing user
        String email = "user@example.com";
        String password = "password123";
        createTestUser(email, password, com.cbt.platform.user.entity.UserRole.USER);

        // When
        LoginRequest request = new LoginRequest(email, password);
        ResponseEntity<AuthResponse> response = restTemplate.exchange(
                "/api/auth/login",
                POST,
                withoutAuth(request),
                AuthResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isNotBlank();
        assertThat(response.getBody().refreshToken()).isNotBlank();
        assertThat(response.getBody().user().email()).isEqualTo(email);
    }

    @Test
    @DisplayName("Should return 401 when password is incorrect")
    void shouldReturn401WhenPasswordIncorrect() {
        // Given - existing user
        String email = "user@example.com";
        createTestUser(email, "correctPassword", com.cbt.platform.user.entity.UserRole.USER);

        // When - login with wrong password
        LoginRequest request = new LoginRequest(email, "wrongPassword");
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/auth/login",
                POST,
                withoutAuth(request),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should return 401 when user does not exist")
    void shouldReturn401WhenUserNotFound() {
        // When
        LoginRequest request = new LoginRequest("nonexistent@example.com", "password");
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/auth/login",
                POST,
                withoutAuth(request),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should return 401 when trying to login as deleted user")
    void shouldReturn401WhenUserDeleted() {
        // Given - user that will be soft deleted
        String email = "deleted@example.com";
        var user = createTestUser(email, "password", com.cbt.platform.user.entity.UserRole.USER);
        userRepository.softDelete(user.getId());

        // When
        LoginRequest request = new LoginRequest(email, "password");
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/auth/login",
                POST,
                withoutAuth(request),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // ==================== JWT Token Tests ====================

    @Test
    @DisplayName("Should access protected endpoint with valid JWT token")
    void shouldAccessProtectedEndpointWithValidToken() {
        // Given - user with valid token
        var user = createTestUser();
        String token = generateAccessToken(user);

        // When - access protected endpoint
        ResponseEntity<UserResponse> response = restTemplate.exchange(
                "/api/users/" + user.getId(),
                org.springframework.http.HttpMethod.GET,
                withAuth(token),
                UserResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should return 401 when accessing protected endpoint without token")
    void shouldReturn401WhenNoToken() {
        // Given - user ID
        var user = createTestUser();

        // When - try to access without token
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/users/" + user.getId(),
                org.springframework.http.HttpMethod.GET,
                withAuth(null, ""),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should return 401 when accessing protected endpoint with invalid token")
    void shouldReturn401WhenInvalidToken() {
        // Given - user ID
        var user = createTestUser();

        // When - try to access with invalid token
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/users/" + user.getId(),
                org.springframework.http.HttpMethod.GET,
                withAuth(null, "invalid.jwt.token"),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // ==================== Role-Based Access Tests ====================

    @Test
    @DisplayName("Should allow admin to access admin-only endpoints")
    void shouldAllowAdminAccessToAdminEndpoints() {
        // Given - admin user
        var adminUser = createAdminUser();
        String adminToken = generateAccessToken(adminUser);

        // When - access admin endpoint
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/users?page=0&size=10",
                org.springframework.http.HttpMethod.GET,
                withAuth(adminToken),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should deny regular user access to admin-only endpoints")
    void shouldDenyRegularUserAccessToAdminEndpoints() {
        // Given - regular user
        var regularUser = createTestUser();
        String userToken = generateAccessToken(regularUser);

        // When - try to access admin endpoint
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/users?page=0&size=10",
                org.springframework.http.HttpMethod.GET,
                withAuth(userToken),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    // ==================== Case Sensitivity Tests ====================

    @Test
    @DisplayName("Should login with email case-insensitive")
    void shouldLoginWithCaseInsensitiveEmail() {
        // Given - user registered with lowercase email
        String email = "user@example.com";
        String password = "password123";
        createTestUser(email, password, com.cbt.platform.user.entity.UserRole.USER);

        // When - login with uppercase email
        LoginRequest request = new LoginRequest("USER@EXAMPLE.COM", password);
        ResponseEntity<AuthResponse> response = restTemplate.exchange(
                "/api/auth/login",
                POST,
                withoutAuth(request),
                AuthResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Should prevent duplicate registration with case-insensitive email")
    void shouldPreventDuplicateRegistrationCaseInsensitive() {
        // Given - user registered with lowercase email
        createTestUser("user@example.com", "password", com.cbt.platform.user.entity.UserRole.USER);

        // When - try to register with uppercase email
        RegisterRequest request = new RegisterRequest(
                "USER@EXAMPLE.COM",
                "password123",
                "User",
                null,  // phone
                null,  // timezone
                null   // language
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/auth/register",
                POST,
                withoutAuth(request),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
