package com.cbt.platform.e2e;

import com.cbt.platform.integration.BaseIntegrationTest;
import com.cbt.platform.user.dto.UpdateProfileRequest;
import com.cbt.platform.user.dto.UserResponse;
import com.cbt.platform.user.entity.User;
import com.cbt.platform.user.entity.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.*;

/**
 * E2E tests for complete user management flow
 * Tests CRUD operations, profile updates, and admin operations
 */
@DisplayName("User Management Flow E2E Tests")
class UserManagementFlowTest extends BaseIntegrationTest {

    private User regularUser;
    private User adminUser;
    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        regularUser = createTestUser("user@example.com", "password", UserRole.USER);
        adminUser = createAdminUser();

        userToken = generateAccessToken(regularUser);
        adminToken = generateAccessToken(adminUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    // ==================== Complete User Lifecycle Flow ====================

    @Test
    @DisplayName("Complete flow: Create → Read → Update → Delete user")
    void shouldCompleteUserLifecycleFlow() {
        // Step 1: Create user (already done in setUp)
        assertThat(regularUser.getId()).isNotNull();
        assertThat(regularUser.getEmail()).isEqualTo("user@example.com");

        // Step 2: Read user profile
        ResponseEntity<UserResponse> getResponse = restTemplate.exchange(
                "/api/users/" + regularUser.getId(),
                GET,
                withAuth(userToken),
                UserResponse.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().email()).isEqualTo("user@example.com");

        // Step 3: Update user profile
        UpdateProfileRequest updateRequest = new UpdateProfileRequest(
                "Updated Name",
                null,  // phone
                "America/New_York",
                "es"
        );

        ResponseEntity<UserResponse> updateResponse = restTemplate.exchange(
                "/api/users/" + regularUser.getId(),
                PUT,
                withAuth(updateRequest, userToken),
                UserResponse.class
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotNull();
        assertThat(updateResponse.getBody().name()).isEqualTo("Updated Name");
        assertThat(updateResponse.getBody().timezone()).isEqualTo("America/New_York");
        assertThat(updateResponse.getBody().preferredLanguage()).isEqualTo("es");

        // Step 4: Verify update in database
        User updatedUser = userRepository.findById(regularUser.getId()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");

        // Step 5: Admin deletes user (soft delete)
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/users/" + regularUser.getId(),
                DELETE,
                withAuth(adminToken),
                Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Step 6: Verify user is soft deleted
        User deletedUser = userRepository.findById(regularUser.getId()).orElseThrow();
        assertThat(deletedUser.getDeletedAt()).isNotNull();
        assertThat(deletedUser.getIsActive()).isFalse();

        // Step 7: Verify deleted user cannot be accessed
        ResponseEntity<String> accessDeletedResponse = restTemplate.exchange(
                "/api/users/" + regularUser.getId(),
                GET,
                withAuth(adminToken),
                String.class
        );

        assertThat(accessDeletedResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ==================== Get User Tests ====================

    @Test
    @DisplayName("Should get user by ID")
    void shouldGetUserById() {
        // When
        ResponseEntity<UserResponse> response = restTemplate.exchange(
                "/api/users/" + regularUser.getId(),
                GET,
                withAuth(userToken),
                UserResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(regularUser.getId());
        assertThat(response.getBody().email()).isEqualTo(regularUser.getEmail());
        assertThat(response.getBody().name()).isEqualTo(regularUser.getName());
    }

    @Test
    @DisplayName("Should return 404 when user not found")
    void shouldReturn404WhenUserNotFound() {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/users/" + java.util.UUID.randomUUID(),
                GET,
                withAuth(userToken),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 401 when accessing user without authentication")
    void shouldReturn401WhenNotAuthenticated() {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/users/" + regularUser.getId(),
                GET,
                withoutAuth(null),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // ==================== Get User by Email Tests (Admin Only) ====================

    @Test
    @DisplayName("Admin should get user by email")
    void adminShouldGetUserByEmail() {
        // When
        ResponseEntity<UserResponse> response = restTemplate.exchange(
                "/api/users/email/" + regularUser.getEmail(),
                GET,
                withAuth(adminToken),
                UserResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().email()).isEqualTo(regularUser.getEmail());
    }

    @Test
    @DisplayName("Regular user should not access get user by email endpoint")
    void regularUserShouldNotGetUserByEmail() {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/users/email/" + regularUser.getEmail(),
                GET,
                withAuth(userToken),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    // ==================== Get All Users Tests (Admin Only) ====================

    @Test
    @DisplayName("Admin should get paginated list of all users")
    void adminShouldGetAllUsers() {
        // Given - create additional users
        createTestUser("user2@example.com", "password", UserRole.USER);
        createTestUser("user3@example.com", "password", UserRole.USER);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/users?page=0&size=10",
                GET,
                withAuth(adminToken),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("\"totalElements\"");
        assertThat(response.getBody()).contains("user@example.com");
    }

    @Test
    @DisplayName("Admin should get paginated users with custom page size")
    void adminShouldGetPaginatedUsers() {
        // Given - create multiple users
        for (int i = 1; i <= 5; i++) {
            createTestUser("user" + i + "@example.com", "password", UserRole.USER);
        }

        // When - request first page with size 2
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/users?page=0&size=2",
                GET,
                withAuth(adminToken),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"size\":2");
    }

    @Test
    @DisplayName("Regular user should not access all users endpoint")
    void regularUserShouldNotGetAllUsers() {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/users?page=0&size=10",
                GET,
                withAuth(userToken),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    // ==================== Update Profile Tests ====================

    @Test
    @DisplayName("User should update own profile")
    void userShouldUpdateOwnProfile() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                "New Name",
                null,  // phone
                "Europe/London",
                "fr"
        );

        // When
        ResponseEntity<UserResponse> response = restTemplate.exchange(
                "/api/users/" + regularUser.getId(),
                PUT,
                withAuth(request, userToken),
                UserResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("New Name");
        assertThat(response.getBody().timezone()).isEqualTo("Europe/London");
        assertThat(response.getBody().preferredLanguage()).isEqualTo("fr");

        // Verify in database
        User updated = userRepository.findById(regularUser.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getTimezone()).isEqualTo("Europe/London");
        assertThat(updated.getPreferredLanguage()).isEqualTo("fr");
    }

    @Test
    @DisplayName("Should update profile with partial data")
    void shouldUpdateProfileWithPartialData() {
        // Given - update only full name
        UpdateProfileRequest request = new UpdateProfileRequest(
                "Only Name Changed",
                null,  // phone
                null,  // timezone
                null   // language
        );

        // When
        ResponseEntity<UserResponse> response = restTemplate.exchange(
                "/api/users/" + regularUser.getId(),
                PUT,
                withAuth(request, userToken),
                UserResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Only Name Changed");
        // Other fields should remain unchanged
        assertThat(response.getBody().timezone()).isEqualTo(regularUser.getTimezone());
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent user")
    void shouldReturn404WhenUpdatingNonExistentUser() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest(
                "New Name",
                null,  // phone
                null,  // timezone
                null   // language
        );

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/users/" + java.util.UUID.randomUUID(),
                PUT,
                withAuth(request, userToken),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ==================== Delete User Tests (Admin Only) ====================

    @Test
    @DisplayName("Admin should delete user (soft delete)")
    void adminShouldDeleteUser() {
        // Given - create user to delete
        User userToDelete = createTestUser("todelete@example.com", "password", UserRole.USER);

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/users/" + userToDelete.getId(),
                DELETE,
                withAuth(adminToken),
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify soft delete in database
        User deletedUser = userRepository.findById(userToDelete.getId()).orElseThrow();
        assertThat(deletedUser.getDeletedAt()).isNotNull();
        assertThat(deletedUser.getIsActive()).isFalse();

        // Verify cannot be found by active queries
        assertThat(userRepository.findActiveById(userToDelete.getId())).isEmpty();
    }

    @Test
    @DisplayName("Regular user should not delete users")
    void regularUserShouldNotDeleteUsers() {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/users/" + adminUser.getId(),
                DELETE,
                withAuth(userToken),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        // Verify user still exists
        assertThat(userRepository.findById(adminUser.getId())).isPresent();
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent user")
    void shouldReturn404WhenDeletingNonExistentUser() {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/users/" + java.util.UUID.randomUUID(),
                DELETE,
                withAuth(adminToken),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 404 when trying to delete already deleted user")
    void shouldReturn404WhenDeletingAlreadyDeletedUser() {
        // Given - soft deleted user
        User userToDelete = createTestUser("deleted@example.com", "password", UserRole.USER);
        userRepository.softDelete(userToDelete.getId());

        // When - try to delete again
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/users/" + userToDelete.getId(),
                DELETE,
                withAuth(adminToken),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ==================== Cross-User Access Tests ====================

    @Test
    @DisplayName("User can access any user's public profile")
    void userCanAccessOtherUsersProfile() {
        // Given - another user
        User otherUser = createTestUser("other@example.com", "password", UserRole.USER);

        // When - regular user accesses other user's profile
        ResponseEntity<UserResponse> response = restTemplate.exchange(
                "/api/users/" + otherUser.getId(),
                GET,
                withAuth(userToken),
                UserResponse.class
        );

        // Then - should be allowed (public profile)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(otherUser.getId());
    }

    @Test
    @DisplayName("User can update any user profile (no ownership check)")
    void userCanUpdateAnyUserProfile() {
        // Given - another user
        User otherUser = createTestUser("other@example.com", "password", UserRole.USER);
        UpdateProfileRequest request = new UpdateProfileRequest(
                "Modified by another user",
                null,  // phone
                null,  // timezone
                null   // language
        );

        // When - regular user updates other user's profile
        ResponseEntity<UserResponse> response = restTemplate.exchange(
                "/api/users/" + otherUser.getId(),
                PUT,
                withAuth(request, userToken),
                UserResponse.class
        );

        // Then - should be allowed (no ownership check in current implementation)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Note: This might be a security concern in real applications
        // Consider adding ownership validation in future
    }

    // ==================== Data Integrity Tests ====================

    @Test
    @DisplayName("Profile updates should preserve email and role")
    void profileUpdatesShouldPreserveEmailAndRole() {
        // Given
        String originalEmail = regularUser.getEmail();
        UserRole originalRole = regularUser.getRole();

        UpdateProfileRequest request = new UpdateProfileRequest(
                "New Name",
                null,  // phone
                "UTC",
                "en"
        );

        // When
        restTemplate.exchange(
                "/api/users/" + regularUser.getId(),
                PUT,
                withAuth(request, userToken),
                UserResponse.class
        );

        // Then - email and role should not change
        User updated = userRepository.findById(regularUser.getId()).orElseThrow();
        assertThat(updated.getEmail()).isEqualTo(originalEmail);
        assertThat(updated.getRole()).isEqualTo(originalRole);
        assertThat(updated.getName()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("Deleted users should not appear in active user queries")
    void deletedUsersShouldNotAppearInActiveQueries() {
        // Given - create and delete a user
        User userToDelete = createTestUser("deleted@example.com", "password", UserRole.USER);
        userRepository.softDelete(userToDelete.getId());

        // When - query for active users
        List<User> activeUsers = userRepository.findAllByIsActiveTrueAndDeletedAtIsNull();

        // Then - deleted user should not be in list
        assertThat(activeUsers)
                .extracting(User::getId)
                .doesNotContain(userToDelete.getId());
    }
}
