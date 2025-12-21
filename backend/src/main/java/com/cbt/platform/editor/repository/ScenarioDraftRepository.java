package com.cbt.platform.editor.repository;

import com.cbt.platform.editor.entity.DraftStatus;
import com.cbt.platform.editor.entity.ScenarioDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ScenarioDraft entity operations
 */
@Repository
public interface ScenarioDraftRepository extends JpaRepository<ScenarioDraft, UUID> {

    /**
     * Find all active drafts (not soft-deleted)
     */
    @Query("SELECT d FROM ScenarioDraft d WHERE d.deletedAt IS NULL ORDER BY d.updatedAt DESC")
    List<ScenarioDraft> findAllActive();

    /**
     * Find drafts by status (not soft-deleted)
     */
    @Query("SELECT d FROM ScenarioDraft d WHERE d.status = :status AND d.deletedAt IS NULL ORDER BY d.updatedAt DESC")
    List<ScenarioDraft> findByStatus(@Param("status") DraftStatus status);

    /**
     * Find drafts by category (not soft-deleted)
     */
    @Query("SELECT d FROM ScenarioDraft d WHERE d.category = :category AND d.deletedAt IS NULL ORDER BY d.updatedAt DESC")
    List<ScenarioDraft> findByCategory(@Param("category") String category);

    /**
     * Find active draft by ID (not soft-deleted)
     */
    @Query("SELECT d FROM ScenarioDraft d WHERE d.id = :id AND d.deletedAt IS NULL")
    Optional<ScenarioDraft> findActiveById(@Param("id") UUID id);

    /**
     * Find drafts created by user (not soft-deleted)
     */
    @Query("SELECT d FROM ScenarioDraft d WHERE d.createdBy.id = :userId AND d.deletedAt IS NULL ORDER BY d.updatedAt DESC")
    List<ScenarioDraft> findByCreatedByUserId(@Param("userId") UUID userId);

    /**
     * Find draft by slug (not soft-deleted)
     */
    @Query("SELECT d FROM ScenarioDraft d WHERE d.slug = :slug AND d.deletedAt IS NULL")
    Optional<ScenarioDraft> findBySlug(@Param("slug") String slug);

    /**
     * Check if draft exists by slug (excluding specific draft ID)
     */
    @Query("SELECT COUNT(d) > 0 FROM ScenarioDraft d WHERE d.slug = :slug AND d.id != :excludeId AND d.deletedAt IS NULL")
    boolean existsBySlugExcludingId(@Param("slug") String slug, @Param("excludeId") UUID excludeId);

    /**
     * Check if draft exists by slug
     */
    @Query("SELECT COUNT(d) > 0 FROM ScenarioDraft d WHERE d.slug = :slug AND d.deletedAt IS NULL")
    boolean existsBySlug(@Param("slug") String slug);

    /**
     * Soft delete draft
     */
    @Modifying
    @Query("UPDATE ScenarioDraft d SET d.deletedAt = CURRENT_TIMESTAMP WHERE d.id = :id")
    void softDelete(@Param("id") UUID id);
}
