package com.cbt.platform.editor.entity;

import com.cbt.platform.user.entity.User;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Version history entry for a ScenarioDraft
 * Tracks all changes made to a draft for audit and rollback purposes
 */
@Entity
@Table(name = "scenario_draft_versions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioDraftVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Parent draft
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draft_id", nullable = false)
    private ScenarioDraft draft;

    /**
     * Sequential version number for this draft (1, 2, 3, ...)
     */
    @Column(nullable = false)
    private Integer versionNumber;

    /**
     * Snapshot of scenario JSON at this version
     */
    @Column(columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode scenarioJson;

    /**
     * Optional description of changes made in this version
     */
    @Column(length = 500)
    private String changeDescription;

    /**
     * User who created this version
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
