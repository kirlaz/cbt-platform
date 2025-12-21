package com.cbt.platform.editor.repository;

import com.cbt.platform.editor.entity.BlockTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for BlockTemplate entity operations
 */
@Repository
public interface BlockTemplateRepository extends JpaRepository<BlockTemplate, UUID> {

    /**
     * Find all active templates (not soft-deleted)
     */
    @Query("SELECT t FROM BlockTemplate t WHERE t.isActive = true AND t.deletedAt IS NULL ORDER BY t.usageCount DESC, t.name ASC")
    List<BlockTemplate> findAllActive();

    /**
     * Find templates by block type (not soft-deleted)
     */
    @Query("SELECT t FROM BlockTemplate t WHERE t.blockType = :blockType AND t.isActive = true AND t.deletedAt IS NULL ORDER BY t.usageCount DESC")
    List<BlockTemplate> findByBlockType(@Param("blockType") String blockType);

    /**
     * Find templates by category (not soft-deleted)
     */
    @Query("SELECT t FROM BlockTemplate t WHERE t.category = :category AND t.isActive = true AND t.deletedAt IS NULL ORDER BY t.usageCount DESC")
    List<BlockTemplate> findByCategory(@Param("category") String category);

    /**
     * Find active template by ID (not soft-deleted)
     */
    @Query("SELECT t FROM BlockTemplate t WHERE t.id = :id AND t.deletedAt IS NULL")
    Optional<BlockTemplate> findActiveById(@Param("id") UUID id);

    /**
     * Find all system templates (not soft-deleted)
     */
    @Query("SELECT t FROM BlockTemplate t WHERE t.isSystem = true AND t.deletedAt IS NULL ORDER BY t.name ASC")
    List<BlockTemplate> findAllSystem();

    /**
     * Increment usage count
     */
    @Modifying
    @Query("UPDATE BlockTemplate t SET t.usageCount = t.usageCount + 1 WHERE t.id = :id")
    void incrementUsageCount(@Param("id") UUID id);

    /**
     * Soft delete template (only if not system template)
     */
    @Modifying
    @Query("UPDATE BlockTemplate t SET t.deletedAt = CURRENT_TIMESTAMP, t.isActive = false WHERE t.id = :id AND t.isSystem = false")
    int softDelete(@Param("id") UUID id);
}
