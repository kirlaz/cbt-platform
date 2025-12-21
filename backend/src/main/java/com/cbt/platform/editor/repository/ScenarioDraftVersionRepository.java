package com.cbt.platform.editor.repository;

import com.cbt.platform.editor.entity.ScenarioDraftVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ScenarioDraftVersion entity operations
 */
@Repository
public interface ScenarioDraftVersionRepository extends JpaRepository<ScenarioDraftVersion, UUID> {

    /**
     * Find all versions for a draft, ordered by version number descending
     */
    @Query("SELECT v FROM ScenarioDraftVersion v WHERE v.draft.id = :draftId ORDER BY v.versionNumber DESC")
    List<ScenarioDraftVersion> findByDraftIdOrderByVersionNumberDesc(@Param("draftId") UUID draftId);

    /**
     * Find latest version for a draft
     */
    @Query("SELECT v FROM ScenarioDraftVersion v WHERE v.draft.id = :draftId ORDER BY v.versionNumber DESC LIMIT 1")
    Optional<ScenarioDraftVersion> findLatestByDraftId(@Param("draftId") UUID draftId);

    /**
     * Find specific version by draft ID and version number
     */
    @Query("SELECT v FROM ScenarioDraftVersion v WHERE v.draft.id = :draftId AND v.versionNumber = :versionNumber")
    Optional<ScenarioDraftVersion> findByDraftIdAndVersionNumber(@Param("draftId") UUID draftId, @Param("versionNumber") Integer versionNumber);

    /**
     * Get next version number for a draft
     */
    @Query("SELECT COALESCE(MAX(v.versionNumber), 0) + 1 FROM ScenarioDraftVersion v WHERE v.draft.id = :draftId")
    Integer getNextVersionNumber(@Param("draftId") UUID draftId);

    /**
     * Count versions for a draft
     */
    @Query("SELECT COUNT(v) FROM ScenarioDraftVersion v WHERE v.draft.id = :draftId")
    Long countByDraftId(@Param("draftId") UUID draftId);

    /**
     * Delete all versions for a draft (cascade on draft deletion)
     */
    @Query("DELETE FROM ScenarioDraftVersion v WHERE v.draft.id = :draftId")
    void deleteByDraftId(@Param("draftId") UUID draftId);
}
