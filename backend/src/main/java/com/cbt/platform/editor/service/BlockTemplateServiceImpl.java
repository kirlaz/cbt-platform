package com.cbt.platform.editor.service;

import com.cbt.platform.editor.dto.BlockTemplateResponse;
import com.cbt.platform.editor.dto.CreateBlockTemplateRequest;
import com.cbt.platform.editor.dto.UpdateBlockTemplateRequest;
import com.cbt.platform.editor.entity.BlockTemplate;
import com.cbt.platform.editor.exception.BlockTemplateNotFoundException;
import com.cbt.platform.editor.exception.SystemTemplateException;
import com.cbt.platform.editor.mapper.BlockTemplateMapper;
import com.cbt.platform.editor.repository.BlockTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of BlockTemplateService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BlockTemplateServiceImpl implements BlockTemplateService {

    private final BlockTemplateRepository templateRepository;
    private final BlockTemplateMapper templateMapper;

    @Override
    @Transactional(readOnly = true)
    public List<BlockTemplateResponse> findAllActive() {
        log.debug("Fetching all active block templates");
        return templateRepository.findAllActive()
                .stream()
                .map(templateMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlockTemplateResponse> findByBlockType(String blockType) {
        log.debug("Fetching block templates by type: {}", blockType);
        return templateRepository.findByBlockType(blockType)
                .stream()
                .map(templateMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlockTemplateResponse> findByCategory(String category) {
        log.debug("Fetching block templates by category: {}", category);
        return templateRepository.findByCategory(category)
                .stream()
                .map(templateMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BlockTemplateResponse findById(UUID id) {
        log.debug("Fetching block template by id: {}", id);
        return templateRepository.findActiveById(id)
                .map(templateMapper::toResponse)
                .orElseThrow(() -> new BlockTemplateNotFoundException(id));
    }

    @Override
    @Transactional
    public BlockTemplateResponse create(CreateBlockTemplateRequest request) {
        log.info("Creating block template: {}", request.name());

        BlockTemplate template = templateMapper.toEntity(request);
        template = templateRepository.save(template);

        log.info("Block template created: id={}", template.getId());
        return templateMapper.toResponse(template);
    }

    @Override
    @Transactional
    public BlockTemplateResponse update(UUID id, UpdateBlockTemplateRequest request) {
        log.info("Updating block template: {}", id);

        BlockTemplate template = templateRepository.findActiveById(id)
                .orElseThrow(() -> new BlockTemplateNotFoundException(id));

        if (template.getIsSystem()) {
            throw new SystemTemplateException();
        }

        templateMapper.updateEntity(template, request);
        template = templateRepository.save(template);

        log.info("Block template updated: id={}", template.getId());
        return templateMapper.toResponse(template);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting block template: {}", id);

        BlockTemplate template = templateRepository.findActiveById(id)
                .orElseThrow(() -> new BlockTemplateNotFoundException(id));

        if (template.getIsSystem()) {
            throw new SystemTemplateException();
        }

        int deleted = templateRepository.softDelete(id);
        if (deleted == 0) {
            throw new SystemTemplateException();
        }

        log.info("Block template deleted: id={}", id);
    }

    @Override
    @Transactional
    public void incrementUsageCount(UUID id) {
        log.debug("Incrementing usage count for template: {}", id);
        templateRepository.incrementUsageCount(id);
    }
}
