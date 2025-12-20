package com.cbt.platform.progress.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * UserProgress entity - tracks user's progress through a course
 *
 * This is the core state management entity that stores:
 * - Current position in the course (session + block)
 * - User data accumulated during the course (JSONB)
 * - Completion history
 */
@Entity
@Table(
    name = "user_progress",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id"})
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * User who is taking the course
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * Course being taken
     */
    @Column(name = "course_id", nullable = false)
    private UUID courseId;

    /**
     * Current session ID (e.g., "onboarding", "session_1")
     */
    @Column(name = "current_session_id", length = 100)
    private String currentSessionId;

    /**
     * Current block index within the session (0-based)
     */
    @Column(name = "current_block_index")
    @Builder.Default
    private Integer currentBlockIndex = 0;

    /**
     * User data accumulated during the course (JSONB)
     *
     * This is the central personalization structure that contains:
     * - User inputs: name, age, triggers, goals
     * - Assessment results: gad7_score, anxiety_levels
     * - Journal entries: thought_records[], exercises[]
     * - Progress tracking: techniques_learned[], sessions_completed[]
     *
     * Used in:
     * - Template resolution: {{name}} → "Иван"
     * - LLM prompts: personalized responses based on user data
     * - Conditional navigation: next block based on answers
     */
    @Column(columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private JsonNode userData = null;

    /**
     * List of completed session IDs
     */
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<String> completedSessions = new ArrayList<>();

    /**
     * List of completed block IDs (format: "session_id:block_id")
     */
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<String> completedBlocks = new ArrayList<>();

    /**
     * Overall course completion percentage (0-100)
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer completionPercentage = 0;

    /**
     * Whether the course is completed
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;

    /**
     * When the course was started
     */
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    /**
     * When the course was completed
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * Last activity timestamp
     */
    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
