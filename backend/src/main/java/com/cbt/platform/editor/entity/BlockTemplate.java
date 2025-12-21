package com.cbt.platform.editor.entity;

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
 * BlockTemplate entity representing reusable block templates
 * Used by content creators to quickly add common blocks to scenarios
 */
@Entity
@Table(name = "block_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Template name (e.g., "Welcome Message", "Stress Level Assessment")
     */
    @Column(nullable = false, length = 200)
    private String name;

    /**
     * Template description
     */
    @Column(length = 500)
    private String description;

    /**
     * Block type (e.g., "STATIC", "INPUT", "LLM_CONVERSATION")
     */
    @Column(nullable = false, length = 50)
    private String blockType;

    /**
     * Category for organizing templates (e.g., "welcome", "assessment", "exercise")
     */
    @Column(length = 50)
    private String category;

    /**
     * Block JSON template (same structure as a block in scenario)
     * Contains: id, type, content, config, next, conditionalNext, etc.
     */
    @Column(columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode blockJson;

    /**
     * Whether this is a system template (cannot be deleted)
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isSystem = false;

    /**
     * Template active status
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Usage count (how many times this template was used)
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer usageCount = 0;

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
