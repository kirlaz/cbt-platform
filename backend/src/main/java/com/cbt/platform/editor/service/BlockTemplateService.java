package com.cbt.platform.editor.service;

import com.cbt.platform.editor.dto.BlockTemplateResponse;
import com.cbt.platform.editor.dto.CreateBlockTemplateRequest;
import com.cbt.platform.editor.dto.UpdateBlockTemplateRequest;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for block template operations
 */
public interface BlockTemplateService {

    /**
     * Find all active block templates
     *
     * @return list of active templates
     */
    List<BlockTemplateResponse> findAllActive();

    /**
     * Find templates by block type
     *
     * @param blockType block type
     * @return list of templates
     */
    List<BlockTemplateResponse> findByBlockType(String blockType);

    /**
     * Find templates by category
     *
     * @param category category
     * @return list of templates
     */
    List<BlockTemplateResponse> findByCategory(String category);

    /**
     * Find template by ID
     *
     * @param id template ID
     * @return template response
     */
    BlockTemplateResponse findById(UUID id);

    /**
     * Create new block template
     *
     * @param request creation request
     * @return created template
     */
    BlockTemplateResponse create(CreateBlockTemplateRequest request);

    /**
     * Update existing block template
     *
     * @param id      template ID
     * @param request update request
     * @return updated template
     */
    BlockTemplateResponse update(UUID id, UpdateBlockTemplateRequest request);

    /**
     * Delete block template (soft delete)
     * Cannot delete system templates
     *
     * @param id template ID
     */
    void delete(UUID id);

    /**
     * Increment usage count when template is used
     *
     * @param id template ID
     */
    void incrementUsageCount(UUID id);
}
