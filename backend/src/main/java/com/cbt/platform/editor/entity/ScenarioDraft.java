package com.cbt.platform.editor.entity;

import com.cbt.platform.course.entity.Course;
import com.cbt.platform.user.entity.User;
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
import java.util.UUID;

/**
 * ScenarioDraft entity representing a draft scenario before publication
 * Used by content creators to build and validate course scenarios
 */
@Entity
@Table(name = "scenario_drafts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Draft name (user-friendly title)
     */
    @Column(nullable = false, length = 200)
    private String name;

    /**
     * Course slug (will be used when publishing to Course)
     */
    @Column(length = 100)
    private String slug;

    /**
     * Course category (e.g., "anxiety", "depression", "self-esteem")
     */
    @Column(length = 50)
    private String category;

    /**
     * Scenario version (e.g., "1.0.1")
     */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String version = "1.0.0";

    /**
     * Full scenario JSON (same structure as Course.scenarioJson)
     * Contains: meta, global_config, user_profile_schema, sessions
     */
    @Column(columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode scenarioJson;

    /**
     * Draft status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DraftStatus status = DraftStatus.DRAFT;

    /**
     * Validation status
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isValid = false;

    /**
     * Validation errors (if any)
     */
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode validationErrors;

    /**
     * User who created this draft
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    /**
     * User who last modified this draft
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_modified_by_user_id")
    private User lastModifiedBy;

    /**
     * Published course (if this draft was published)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "published_course_id")
    private Course publishedCourse;

    /**
     * Publication timestamp
     */
    private LocalDateTime publishedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Soft delete timestamp
     */
    private LocalDateTime deletedAt;
}
