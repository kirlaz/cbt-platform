package com.cbt.platform.unit.progress;

import com.cbt.platform.course.exception.CourseNotFoundException;
import com.cbt.platform.course.repository.CourseRepository;
import com.cbt.platform.fixtures.TestDataBuilder;
import com.cbt.platform.progress.dto.*;
import com.cbt.platform.progress.entity.UserProgress;
import com.cbt.platform.progress.exception.CourseAlreadyStartedException;
import com.cbt.platform.progress.exception.ProgressNotFoundException;
import com.cbt.platform.progress.mapper.ProgressMapper;
import com.cbt.platform.progress.repository.ProgressRepository;
import com.cbt.platform.progress.service.ProgressServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProgressServiceImpl
 * Tests business logic in isolation using mocks
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProgressService Unit Tests")
class ProgressServiceTest {

    @Mock
    private ProgressRepository progressRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ProgressMapper progressMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProgressServiceImpl progressService;

    private UUID userId;
    private UUID courseId;
    private UserProgress testProgress;
    private ProgressResponse testResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        courseId = UUID.randomUUID();
        testProgress = TestDataBuilder.userProgress(userId, courseId);
        testResponse = new ProgressResponse(
                testProgress.getId(),
                testProgress.getUserId(),
                testProgress.getCourseId(),
                testProgress.getCurrentSessionId(),
                testProgress.getCurrentBlockIndex(),
                testProgress.getUserData(),
                testProgress.getCompletedSessions(),
                testProgress.getCompletedBlocks(),
                testProgress.getCompletionPercentage(),
                testProgress.getIsCompleted(),
                testProgress.getStartedAt(),
                testProgress.getCompletedAt(),
                testProgress.getLastActivityAt()
        );
    }

    // ==================== Start Course Tests ====================

    @Test
    @DisplayName("Should start course successfully when course exists and not started")
    void shouldStartCourseSuccessfully() {
        // Given
        StartCourseRequest request = new StartCourseRequest(courseId);
        ObjectNode emptyUserData = new ObjectMapper().createObjectNode();

        when(courseRepository.existsById(courseId)).thenReturn(true);
        when(progressRepository.existsByUserIdAndCourseId(userId, courseId)).thenReturn(false);
        when(objectMapper.createObjectNode()).thenReturn(emptyUserData);
        when(progressRepository.save(any(UserProgress.class))).thenReturn(testProgress);
        when(progressMapper.toResponse(testProgress)).thenReturn(testResponse);

        // When
        ProgressResponse result = progressService.startCourse(userId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.courseId()).isEqualTo(courseId);

        ArgumentCaptor<UserProgress> captor = ArgumentCaptor.forClass(UserProgress.class);
        verify(progressRepository).save(captor.capture());
        UserProgress savedProgress = captor.getValue();
        assertThat(savedProgress.getUserId()).isEqualTo(userId);
        assertThat(savedProgress.getCourseId()).isEqualTo(courseId);
        assertThat(savedProgress.getCompletionPercentage()).isEqualTo(0);
        assertThat(savedProgress.getIsCompleted()).isFalse();
    }

    @Test
    @DisplayName("Should throw CourseNotFoundException when course does not exist")
    void shouldThrowExceptionWhenCourseNotFound() {
        // Given
        StartCourseRequest request = new StartCourseRequest(courseId);
        when(courseRepository.existsById(courseId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> progressService.startCourse(userId, request))
                .isInstanceOf(CourseNotFoundException.class);

        verify(progressRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw CourseAlreadyStartedException when course already started")
    void shouldThrowExceptionWhenCourseAlreadyStarted() {
        // Given
        StartCourseRequest request = new StartCourseRequest(courseId);
        when(courseRepository.existsById(courseId)).thenReturn(true);
        when(progressRepository.existsByUserIdAndCourseId(userId, courseId)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> progressService.startCourse(userId, request))
                .isInstanceOf(CourseAlreadyStartedException.class);

        verify(progressRepository, never()).save(any());
    }

    // ==================== Get Progress Tests ====================

    @Test
    @DisplayName("Should get progress successfully when exists")
    void shouldGetProgressSuccessfully() {
        // Given
        when(progressRepository.findByUserIdAndCourseId(userId, courseId))
                .thenReturn(Optional.of(testProgress));
        when(progressMapper.toResponse(testProgress)).thenReturn(testResponse);

        // When
        ProgressResponse result = progressService.getProgress(userId, courseId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.courseId()).isEqualTo(courseId);
    }

    @Test
    @DisplayName("Should throw ProgressNotFoundException when progress not found")
    void shouldThrowExceptionWhenProgressNotFound() {
        // Given
        when(progressRepository.findByUserIdAndCourseId(userId, courseId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> progressService.getProgress(userId, courseId))
                .isInstanceOf(ProgressNotFoundException.class);
    }

    @Test
    @DisplayName("Should get all progress for user")
    void shouldGetAllProgressForUser() {
        // Given
        List<UserProgress> progressList = List.of(testProgress);
        when(progressRepository.findByUserId(userId)).thenReturn(progressList);
        when(progressMapper.toResponse(testProgress)).thenReturn(testResponse);

        // When
        List<ProgressResponse> result = progressService.getAllProgressForUser(userId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should get active progress for user")
    void shouldGetActiveProgressForUser() {
        // Given
        List<UserProgress> progressList = List.of(testProgress);
        when(progressRepository.findActiveProgressByUserId(userId)).thenReturn(progressList);
        when(progressMapper.toResponse(testProgress)).thenReturn(testResponse);

        // When
        List<ProgressResponse> result = progressService.getActiveProgressForUser(userId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isCompleted()).isFalse();
    }

    // ==================== Update Progress Tests ====================

    @Test
    @DisplayName("Should update progress successfully")
    void shouldUpdateProgressSuccessfully() {
        // Given
        UpdateProgressRequest request = new UpdateProgressRequest(
                "session_2",
                5,
                50
        );
        when(progressRepository.findByUserIdAndCourseId(userId, courseId))
                .thenReturn(Optional.of(testProgress));
        when(progressRepository.save(testProgress)).thenReturn(testProgress);
        when(progressMapper.toResponse(testProgress)).thenReturn(testResponse);

        // When
        ProgressResponse result = progressService.updateProgress(userId, courseId, request);

        // Then
        assertThat(result).isNotNull();
        verify(progressMapper).updateEntity(testProgress, request);
        verify(progressRepository).save(testProgress);
        assertThat(testProgress.getLastActivityAt()).isNotNull();
    }

    // ==================== Update UserData Tests ====================

    @Test
    @DisplayName("Should merge user data when merge is true")
    void shouldMergeUserDataWhenMergeIsTrue() {
        // Given
        ObjectMapper realObjectMapper = new ObjectMapper();
        progressService = new ProgressServiceImpl(
                progressRepository,
                courseRepository,
                progressMapper,
                realObjectMapper
        );

        ObjectNode existingData = realObjectMapper.createObjectNode();
        existingData.put("name", "John");
        existingData.put("age", 30);

        ObjectNode newData = realObjectMapper.createObjectNode();
        newData.put("age", 31); // Override
        newData.put("city", "New York"); // New field

        testProgress.setUserData(existingData);

        UpdateUserDataRequest request = new UpdateUserDataRequest(newData, true);
        when(progressRepository.findByUserIdAndCourseId(userId, courseId))
                .thenReturn(Optional.of(testProgress));
        when(progressRepository.save(testProgress)).thenReturn(testProgress);
        when(progressMapper.toResponse(testProgress)).thenReturn(testResponse);

        // When
        ProgressResponse result = progressService.updateUserData(userId, courseId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(testProgress.getUserData().get("name").asText()).isEqualTo("John"); // Preserved
        assertThat(testProgress.getUserData().get("age").asInt()).isEqualTo(31); // Updated
        assertThat(testProgress.getUserData().get("city").asText()).isEqualTo("New York"); // Added
    }

    @Test
    @DisplayName("Should replace user data when merge is false")
    void shouldReplaceUserDataWhenMergeIsFalse() {
        // Given
        ObjectMapper realObjectMapper = new ObjectMapper();
        progressService = new ProgressServiceImpl(
                progressRepository,
                courseRepository,
                progressMapper,
                realObjectMapper
        );

        ObjectNode existingData = realObjectMapper.createObjectNode();
        existingData.put("name", "John");
        existingData.put("age", 30);

        ObjectNode newData = realObjectMapper.createObjectNode();
        newData.put("city", "New York");

        testProgress.setUserData(existingData);

        UpdateUserDataRequest request = new UpdateUserDataRequest(newData, false);
        when(progressRepository.findByUserIdAndCourseId(userId, courseId))
                .thenReturn(Optional.of(testProgress));
        when(progressRepository.save(testProgress)).thenReturn(testProgress);
        when(progressMapper.toResponse(testProgress)).thenReturn(testResponse);

        // When
        ProgressResponse result = progressService.updateUserData(userId, courseId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(testProgress.getUserData().has("name")).isFalse(); // Removed
        assertThat(testProgress.getUserData().has("age")).isFalse(); // Removed
        assertThat(testProgress.getUserData().get("city").asText()).isEqualTo("New York"); // New data
    }

    // ==================== Complete Session Tests ====================

    @Test
    @DisplayName("Should complete session successfully")
    void shouldCompleteSessionSuccessfully() {
        // Given
        String sessionId = "session_1";
        CompleteSessionRequest request = new CompleteSessionRequest(sessionId);
        when(progressRepository.findByUserIdAndCourseId(userId, courseId))
                .thenReturn(Optional.of(testProgress));
        when(progressRepository.save(testProgress)).thenReturn(testProgress);
        when(progressMapper.toResponse(testProgress)).thenReturn(testResponse);

        // When
        ProgressResponse result = progressService.completeSession(userId, courseId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(testProgress.getCompletedSessions()).contains(sessionId);
        verify(progressRepository).save(testProgress);
    }

    @Test
    @DisplayName("Should not duplicate session when already completed")
    void shouldNotDuplicateCompletedSession() {
        // Given
        String sessionId = "session_1";
        testProgress.getCompletedSessions().add(sessionId); // Already completed

        CompleteSessionRequest request = new CompleteSessionRequest(sessionId);
        when(progressRepository.findByUserIdAndCourseId(userId, courseId))
                .thenReturn(Optional.of(testProgress));
        when(progressRepository.save(testProgress)).thenReturn(testProgress);
        when(progressMapper.toResponse(testProgress)).thenReturn(testResponse);

        // When
        ProgressResponse result = progressService.completeSession(userId, courseId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(testProgress.getCompletedSessions()).containsOnlyOnce(sessionId);
    }

    // ==================== Complete Block Tests ====================

    @Test
    @DisplayName("Should complete block successfully")
    void shouldCompleteBlockSuccessfully() {
        // Given
        String sessionId = "session_1";
        String blockId = "welcome";
        String expectedBlockKey = "session_1:welcome";

        when(progressRepository.findByUserIdAndCourseId(userId, courseId))
                .thenReturn(Optional.of(testProgress));
        when(progressRepository.save(testProgress)).thenReturn(testProgress);
        when(progressMapper.toResponse(testProgress)).thenReturn(testResponse);

        // When
        ProgressResponse result = progressService.completeBlock(userId, courseId, sessionId, blockId);

        // Then
        assertThat(result).isNotNull();
        assertThat(testProgress.getCompletedBlocks()).contains(expectedBlockKey);
        verify(progressRepository).save(testProgress);
    }

    @Test
    @DisplayName("Should not duplicate block when already completed")
    void shouldNotDuplicateCompletedBlock() {
        // Given
        String sessionId = "session_1";
        String blockId = "welcome";
        String blockKey = "session_1:welcome";
        testProgress.getCompletedBlocks().add(blockKey); // Already completed

        when(progressRepository.findByUserIdAndCourseId(userId, courseId))
                .thenReturn(Optional.of(testProgress));
        when(progressRepository.save(testProgress)).thenReturn(testProgress);
        when(progressMapper.toResponse(testProgress)).thenReturn(testResponse);

        // When
        ProgressResponse result = progressService.completeBlock(userId, courseId, sessionId, blockId);

        // Then
        assertThat(result).isNotNull();
        assertThat(testProgress.getCompletedBlocks()).containsOnlyOnce(blockKey);
    }

    // ==================== Complete Course Tests ====================

    @Test
    @DisplayName("Should complete course successfully")
    void shouldCompleteCourseSuccessfully() {
        // Given
        when(progressRepository.findByUserIdAndCourseId(userId, courseId))
                .thenReturn(Optional.of(testProgress));
        when(progressRepository.save(testProgress)).thenReturn(testProgress);
        when(progressMapper.toResponse(testProgress)).thenReturn(testResponse);

        // When
        ProgressResponse result = progressService.completeCourse(userId, courseId);

        // Then
        assertThat(result).isNotNull();
        assertThat(testProgress.getIsCompleted()).isTrue();
        assertThat(testProgress.getCompletionPercentage()).isEqualTo(100);
        assertThat(testProgress.getCompletedAt()).isNotNull();
        verify(progressRepository).save(testProgress);
    }

    // ==================== Delete Progress Tests ====================

    @Test
    @DisplayName("Should delete progress successfully")
    void shouldDeleteProgressSuccessfully() {
        // Given
        when(progressRepository.findByUserIdAndCourseId(userId, courseId))
                .thenReturn(Optional.of(testProgress));

        // When
        progressService.deleteProgress(userId, courseId);

        // Then
        verify(progressRepository).delete(testProgress);
    }

    @Test
    @DisplayName("Should throw ProgressNotFoundException when deleting non-existent progress")
    void shouldThrowExceptionWhenDeletingNonExistentProgress() {
        // Given
        when(progressRepository.findByUserIdAndCourseId(userId, courseId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> progressService.deleteProgress(userId, courseId))
                .isInstanceOf(ProgressNotFoundException.class);

        verify(progressRepository, never()).delete(any());
    }
}
