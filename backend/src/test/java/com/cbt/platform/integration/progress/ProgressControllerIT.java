package com.cbt.platform.integration.progress;

import com.cbt.platform.course.entity.Course;
import com.cbt.platform.course.repository.CourseRepository;
import com.cbt.platform.fixtures.TestDataBuilder;
import com.cbt.platform.integration.BaseIntegrationTest;
import com.cbt.platform.progress.dto.*;
import com.cbt.platform.progress.entity.UserProgress;
import com.cbt.platform.progress.repository.ProgressRepository;
import com.cbt.platform.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.*;

/**
 * Integration tests for ProgressController
 * Tests full REST API with real database and security
 */
@DisplayName("ProgressController Integration Tests")
class ProgressControllerIT extends BaseIntegrationTest {

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private String userToken;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        // Clean up
        progressRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user and token
        testUser = createTestUser();
        userToken = generateAccessToken(testUser);

        // Create test course
        testCourse = TestDataBuilder.course();
        testCourse = courseRepository.save(testCourse);
    }

    @AfterEach
    void tearDown() {
        progressRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();
    }

    // ==================== Start Course Tests ====================

    @Test
    @DisplayName("POST /api/progress/start - Should start course successfully")
    void shouldStartCourseSuccessfully() {
        // Given
        StartCourseRequest request = new StartCourseRequest(testCourse.getId());

        // When
        ResponseEntity<ProgressResponse> response = restTemplate.exchange(
                "/api/progress/start",
                POST,
                withAuth(request, userToken),
                ProgressResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().userId()).isEqualTo(testUser.getId());
        assertThat(response.getBody().courseId()).isEqualTo(testCourse.getId());
        assertThat(response.getBody().completionPercentage()).isEqualTo(0);
        assertThat(response.getBody().isCompleted()).isFalse();

        // Verify in database
        List<UserProgress> progressList = progressRepository.findByUserId(testUser.getId());
        assertThat(progressList).hasSize(1);
        assertThat(progressList.get(0).getCourseId()).isEqualTo(testCourse.getId());
    }

    @Test
    @DisplayName("POST /api/progress/start - Should return 404 when course not found")
    void shouldReturn404WhenCourseNotFound() {
        // Given
        StartCourseRequest request = new StartCourseRequest(java.util.UUID.randomUUID());

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/progress/start",
                POST,
                withAuth(request, userToken),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("POST /api/progress/start - Should return 409 when course already started")
    void shouldReturn409WhenCourseAlreadyStarted() {
        // Given
        UserProgress existingProgress = TestDataBuilder.userProgress(testUser.getId(), testCourse.getId());
        progressRepository.save(existingProgress);

        StartCourseRequest request = new StartCourseRequest(testCourse.getId());

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/progress/start",
                POST,
                withAuth(request, userToken),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("POST /api/progress/start - Should return 401 when not authenticated")
    void shouldReturn401WhenNotAuthenticated() {
        // Given
        StartCourseRequest request = new StartCourseRequest(testCourse.getId());

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/progress/start",
                POST,
                withoutAuth(request),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // ==================== Get Progress Tests ====================

    @Test
    @DisplayName("GET /api/progress/my - Should get all user progress")
    void shouldGetAllUserProgress() {
        // Given
        UserProgress progress1 = TestDataBuilder.userProgress(testUser.getId(), testCourse.getId());
        Course course2 = courseRepository.save(TestDataBuilder.course("course-2", "Course 2"));
        UserProgress progress2 = TestDataBuilder.userProgress(testUser.getId(), course2.getId());
        progressRepository.saveAll(List.of(progress1, progress2));

        // When
        ResponseEntity<ProgressResponse[]> response = restTemplate.exchange(
                "/api/progress/my",
                GET,
                withAuth(userToken),
                ProgressResponse[].class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    @DisplayName("GET /api/progress/my/active - Should get only active progress")
    void shouldGetOnlyActiveProgress() {
        // Given
        UserProgress activeProgress = TestDataBuilder.userProgress(testUser.getId(), testCourse.getId());
        Course course2 = courseRepository.save(TestDataBuilder.course("course-2", "Course 2"));
        UserProgress completedProgress = TestDataBuilder.completedUserProgress(testUser.getId(), course2.getId());
        progressRepository.saveAll(List.of(activeProgress, completedProgress));

        // When
        ResponseEntity<ProgressResponse[]> response = restTemplate.exchange(
                "/api/progress/my/active",
                GET,
                withAuth(userToken),
                ProgressResponse[].class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].isCompleted()).isFalse();
    }

    @Test
    @DisplayName("GET /api/progress/course/{courseId} - Should get progress in course")
    void shouldGetProgressInCourse() {
        // Given
        UserProgress progress = TestDataBuilder.userProgress(testUser.getId(), testCourse.getId());
        progressRepository.save(progress);

        // When
        ResponseEntity<ProgressResponse> response = restTemplate.exchange(
                "/api/progress/course/" + testCourse.getId(),
                GET,
                withAuth(userToken),
                ProgressResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().courseId()).isEqualTo(testCourse.getId());
    }

    @Test
    @DisplayName("GET /api/progress/course/{courseId} - Should return 404 when no progress")
    void shouldReturn404WhenNoProgress() {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/progress/course/" + testCourse.getId(),
                GET,
                withAuth(userToken),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ==================== Update Progress Tests ====================

    @Test
    @DisplayName("PUT /api/progress/course/{courseId} - Should update progress")
    void shouldUpdateProgress() {
        // Given
        UserProgress progress = TestDataBuilder.userProgress(testUser.getId(), testCourse.getId());
        progressRepository.save(progress);

        UpdateProgressRequest request = new UpdateProgressRequest("session_2", 5, null, 50);

        // When
        ResponseEntity<ProgressResponse> response = restTemplate.exchange(
                "/api/progress/course/" + testCourse.getId(),
                PUT,
                withAuth(request, userToken),
                ProgressResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().currentSessionId()).isEqualTo("session_2");
        assertThat(response.getBody().currentBlockIndex()).isEqualTo(5);
        assertThat(response.getBody().completionPercentage()).isEqualTo(50);

        // Verify in database
        UserProgress updated = progressRepository.findByUserIdAndCourseId(testUser.getId(), testCourse.getId())
                .orElseThrow();
        assertThat(updated.getCurrentSessionId()).isEqualTo("session_2");
        assertThat(updated.getCurrentBlockIndex()).isEqualTo(5);
    }

    // ==================== Update UserData Tests ====================

    @Test
    @DisplayName("PUT /api/progress/course/{courseId}/user-data - Should merge user data")
    void shouldMergeUserData() {
        // Given
        ObjectNode existingData = objectMapper.createObjectNode();
        existingData.put("name", "John");
        existingData.put("age", 30);

        UserProgress progress = TestDataBuilder.userProgressWithData(
                testUser.getId(),
                testCourse.getId(),
                existingData
        );
        progressRepository.save(progress);

        ObjectNode newData = objectMapper.createObjectNode();
        newData.put("age", 31);
        newData.put("city", "New York");

        UpdateUserDataRequest request = new UpdateUserDataRequest(newData, true);

        // When
        ResponseEntity<ProgressResponse> response = restTemplate.exchange(
                "/api/progress/course/" + testCourse.getId() + "/user-data",
                PUT,
                withAuth(request, userToken),
                ProgressResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().userData().get("name").asText()).isEqualTo("John");
        assertThat(response.getBody().userData().get("age").asInt()).isEqualTo(31);
        assertThat(response.getBody().userData().get("city").asText()).isEqualTo("New York");
    }

    @Test
    @DisplayName("PUT /api/progress/course/{courseId}/user-data - Should replace user data")
    void shouldReplaceUserData() {
        // Given
        ObjectNode existingData = objectMapper.createObjectNode();
        existingData.put("name", "John");
        existingData.put("age", 30);

        UserProgress progress = TestDataBuilder.userProgressWithData(
                testUser.getId(),
                testCourse.getId(),
                existingData
        );
        progressRepository.save(progress);

        ObjectNode newData = objectMapper.createObjectNode();
        newData.put("city", "New York");

        UpdateUserDataRequest request = new UpdateUserDataRequest(newData, false);

        // When
        ResponseEntity<ProgressResponse> response = restTemplate.exchange(
                "/api/progress/course/" + testCourse.getId() + "/user-data",
                PUT,
                withAuth(request, userToken),
                ProgressResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().userData().has("name")).isFalse();
        assertThat(response.getBody().userData().has("age")).isFalse();
        assertThat(response.getBody().userData().get("city").asText()).isEqualTo("New York");
    }

    // ==================== Complete Session Tests ====================

    @Test
    @DisplayName("POST /api/progress/course/{courseId}/complete-session - Should complete session")
    void shouldCompleteSession() {
        // Given
        UserProgress progress = TestDataBuilder.userProgress(testUser.getId(), testCourse.getId());
        progressRepository.save(progress);

        CompleteSessionRequest request = new CompleteSessionRequest("session_1");

        // When
        ResponseEntity<ProgressResponse> response = restTemplate.exchange(
                "/api/progress/course/" + testCourse.getId() + "/complete-session",
                POST,
                withAuth(request, userToken),
                ProgressResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().completedSessions()).contains("session_1");

        // Verify in database
        UserProgress updated = progressRepository.findByUserIdAndCourseId(testUser.getId(), testCourse.getId())
                .orElseThrow();
        assertThat(updated.getCompletedSessions()).contains("session_1");
    }

    // ==================== Complete Block Tests ====================

    @Test
    @DisplayName("POST /api/progress/course/{courseId}/complete-block/{sessionId}/{blockId} - Should complete block")
    void shouldCompleteBlock() {
        // Given
        UserProgress progress = TestDataBuilder.userProgress(testUser.getId(), testCourse.getId());
        progressRepository.save(progress);

        // When
        ResponseEntity<ProgressResponse> response = restTemplate.exchange(
                "/api/progress/course/" + testCourse.getId() + "/complete-block/session_1/welcome",
                POST,
                withAuth(userToken),
                ProgressResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().completedBlocks()).contains("session_1:welcome");

        // Verify in database
        UserProgress updated = progressRepository.findByUserIdAndCourseId(testUser.getId(), testCourse.getId())
                .orElseThrow();
        assertThat(updated.getCompletedBlocks()).contains("session_1:welcome");
    }

    // ==================== Complete Course Tests ====================

    @Test
    @DisplayName("POST /api/progress/course/{courseId}/complete - Should complete course")
    void shouldCompleteCourse() {
        // Given
        UserProgress progress = TestDataBuilder.userProgress(testUser.getId(), testCourse.getId());
        progressRepository.save(progress);

        // When
        ResponseEntity<ProgressResponse> response = restTemplate.exchange(
                "/api/progress/course/" + testCourse.getId() + "/complete",
                POST,
                withAuth(userToken),
                ProgressResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isCompleted()).isTrue();
        assertThat(response.getBody().completionPercentage()).isEqualTo(100);
        assertThat(response.getBody().completedAt()).isNotNull();

        // Verify in database
        UserProgress updated = progressRepository.findByUserIdAndCourseId(testUser.getId(), testCourse.getId())
                .orElseThrow();
        assertThat(updated.getIsCompleted()).isTrue();
        assertThat(updated.getCompletionPercentage()).isEqualTo(100);
    }

    // ==================== Delete Progress Tests ====================

    @Test
    @DisplayName("DELETE /api/progress/course/{courseId} - Should delete progress")
    void shouldDeleteProgress() {
        // Given
        UserProgress progress = TestDataBuilder.userProgress(testUser.getId(), testCourse.getId());
        progressRepository.save(progress);

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/progress/course/" + testCourse.getId(),
                DELETE,
                withAuth(userToken),
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify in database
        assertThat(progressRepository.findByUserIdAndCourseId(testUser.getId(), testCourse.getId()))
                .isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/progress/course/{courseId} - Should return 404 when no progress")
    void shouldReturn404WhenDeletingNonExistentProgress() {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/progress/course/" + testCourse.getId(),
                DELETE,
                withAuth(userToken),
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
