package com.cbt.platform.course.entity;

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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Course entity representing a CBT course with JSON scenario
 */
@Entity
@Table(name = "courses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Unique slug identifier for the course (e.g., "anxiety-management")
     */
    @Column(unique = true, nullable = false, length = 100)
    private String slug;

    /**
     * Course display name
     */
    @Column(nullable = false, length = 200)
    private String name;

    /**
     * Course description
     */
    @Column(length = 1000)
    private String description;

    /**
     * Full scenario JSON loaded from resources/scenarios/{slug}/scenario_{version}.json
     * Contains: meta, global_config, user_profile_schema, sessions
     */
    @Column(columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode scenarioJson;

    /**
     * Scenario version (e.g., "1.0.1")
     */
    @Column(nullable = false, length = 20)
    private String version;

    /**
     * Number of free sessions available before paywall
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer freeSessions = 2;

    /**
     * Course price (null if free)
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Course icon/image URL
     */
    @Column(length = 500)
    private String imageUrl;

    /**
     * Estimated duration in minutes
     */
    private Integer estimatedDurationMinutes;

    /**
     * Course category (e.g., "anxiety", "depression", "self-esteem")
     */
    @Column(length = 50)
    private String category;

    /**
     * Course active status
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Course published status
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublished = false;

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
