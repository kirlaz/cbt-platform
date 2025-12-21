package com.cbt.platform.editor.service;

import com.cbt.platform.editor.dto.*;
import com.cbt.platform.editor.entity.DraftStatus;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for scenario draft editor operations
 */
public interface ScenarioEditorService {

    /**
     * Find all active drafts
     *
     * @return list of drafts
     */
    List<DraftResponse> findAllActive();

    /**
     * Find drafts by status
     *
     * @param status draft status
     * @return list of drafts
     */
    List<DraftResponse> findByStatus(DraftStatus status);

    /**
     * Find drafts by category
     *
     * @param category category
     * @return list of drafts
     */
    List<DraftResponse> findByCategory(String category);

    /**
     * Find draft by ID
     *
     * @param id draft ID
     * @return draft detail
     */
    DraftDetailResponse findById(UUID id);

    /**
     * Create new draft
     *
     * @param request creation request
     * @param userId  current user ID
     * @return created draft
     */
    DraftDetailResponse create(CreateDraftRequest request, UUID userId);

    /**
     * Update existing draft
     *
     * @param id      draft ID
     * @param request update request
     * @param userId  current user ID
     * @return updated draft
     */
    DraftDetailResponse update(UUID id, UpdateDraftRequest request, UUID userId);

    /**
     * Delete draft (soft delete)
     *
     * @param id draft ID
     */
    void delete(UUID id);

    /**
     * Validate draft
     *
     * @param id draft ID
     * @return validation result
     */
    ValidationResultResponse validateDraft(UUID id);

    /**
     * Publish draft to course
     *
     * @param id      draft ID
     * @param request publish request
     * @return created course ID
     */
    UUID publishDraft(UUID id, PublishDraftRequest request);

    /**
     * Get draft versions history
     *
     * @param draftId draft ID
     * @return list of versions
     */
    List<DraftVersionResponse> getDraftVersions(UUID draftId);

    /**
     * Get specific draft version
     *
     * @param draftId       draft ID
     * @param versionNumber version number
     * @return version detail
     */
    DraftVersionResponse getDraftVersion(UUID draftId, Integer versionNumber);

    /**
     * Restore draft to specific version
     *
     * @param draftId       draft ID
     * @param versionNumber version number
     * @param userId        current user ID
     * @return updated draft
     */
    DraftDetailResponse restoreVersion(UUID draftId, Integer versionNumber, UUID userId);
}
