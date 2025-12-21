package com.cbt.platform.fixtures;

import com.cbt.platform.course.entity.Course;
import com.cbt.platform.progress.entity.UserProgress;
import com.cbt.platform.user.entity.User;
import com.cbt.platform.user.entity.UserRole;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Test data builder for creating test fixtures
 * Provides consistent test data across unit and integration tests
 */
public class TestDataBuilder {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== User Fixtures ====================

    public static User.UserBuilder defaultUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .passwordHash("$2a$10$hashedPassword")
                .name("Test User")
                .role(UserRole.USER)
                .isActive(true)
                .timezone("UTC")
                .preferredLanguage("en")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now());
    }

    public static User user() {
        return defaultUser().build();
    }

    public static User user(String email) {
        return defaultUser()
                .email(email)
                .build();
    }

    public static User adminUser() {
        return defaultUser()
                .email("admin@example.com")
                .role(UserRole.ADMIN)
                .build();
    }

    // ==================== Course Fixtures ====================

    public static Course.CourseBuilder defaultCourse() {
        return Course.builder()
                .id(UUID.randomUUID())
                .slug("test-course")
                .name("Test Course")
                .description("Test course description")
                .category("test")
                .scenarioJson(objectMapper.createObjectNode())
                .estimatedDurationMinutes(30)
                .version("1.0.0")
                .isActive(true)
                .isPublished(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now());
    }

    public static Course course() {
        return defaultCourse().build();
    }

    public static Course course(String slug, String name) {
        return defaultCourse()
                .slug(slug)
                .name(name)
                .build();
    }

    public static Course premiumCourse() {
        return defaultCourse()
                .slug("premium-course")
                .name("Premium Course")
                .price(java.math.BigDecimal.valueOf(99.99))
                .build();
    }

    // ==================== UserProgress Fixtures ====================

    public static UserProgress.UserProgressBuilder defaultUserProgress() {
        return UserProgress.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .courseId(UUID.randomUUID())
                .currentSessionId(null)
                .currentBlockIndex(0)
                .userData(emptyUserData())
                .completedSessions(new ArrayList<>())
                .completedBlocks(new ArrayList<>())
                .completionPercentage(0)
                .isCompleted(false)
                .startedAt(LocalDateTime.now())
                .lastActivityAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now());
    }

    public static UserProgress userProgress() {
        return defaultUserProgress().build();
    }

    public static UserProgress userProgress(UUID userId, UUID courseId) {
        return defaultUserProgress()
                .userId(userId)
                .courseId(courseId)
                .build();
    }

    public static UserProgress userProgressWithData(UUID userId, UUID courseId, JsonNode userData) {
        return defaultUserProgress()
                .userId(userId)
                .courseId(courseId)
                .userData(userData)
                .build();
    }

    public static UserProgress userProgressInSession(UUID userId, UUID courseId, String sessionId, int blockIndex) {
        return defaultUserProgress()
                .userId(userId)
                .courseId(courseId)
                .currentSessionId(sessionId)
                .currentBlockIndex(blockIndex)
                .build();
    }

    public static UserProgress completedUserProgress(UUID userId, UUID courseId) {
        return defaultUserProgress()
                .userId(userId)
                .courseId(courseId)
                .isCompleted(true)
                .completionPercentage(100)
                .completedAt(LocalDateTime.now())
                .build();
    }

    // ==================== UserData (JSONB) Fixtures ====================

    public static ObjectNode emptyUserData() {
        return objectMapper.createObjectNode();
    }

    public static ObjectNode userDataWithName(String name) {
        ObjectNode userData = objectMapper.createObjectNode();
        userData.put("name", name);
        return userData;
    }

    public static ObjectNode userDataWithStressLevel(int level) {
        ObjectNode userData = objectMapper.createObjectNode();
        userData.put("stressLevel", level);
        return userData;
    }

    public static ObjectNode userDataComplete() {
        ObjectNode userData = objectMapper.createObjectNode();
        userData.put("name", "John");
        userData.put("age", 30);
        userData.put("stressLevel", 7);
        userData.set("triggers", objectMapper.createArrayNode()
                .add("Deadlines")
                .add("Meetings"));
        return userData;
    }

    // ==================== Helper Methods ====================

    public static List<String> completedSessions(String... sessionIds) {
        return new ArrayList<>(List.of(sessionIds));
    }

    public static List<String> completedBlocks(String... blockKeys) {
        return new ArrayList<>(List.of(blockKeys));
    }

    /**
     * Creates a block key in format "sessionId:blockId"
     */
    public static String blockKey(String sessionId, String blockId) {
        return sessionId + ":" + blockId;
    }
}
