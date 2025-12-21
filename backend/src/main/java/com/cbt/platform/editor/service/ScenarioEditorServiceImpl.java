package com.cbt.platform.editor.service;

import com.cbt.platform.course.entity.Course;
import com.cbt.platform.course.exception.CourseAlreadyExistsException;
import com.cbt.platform.course.repository.CourseRepository;
import com.cbt.platform.editor.dto.*;
import com.cbt.platform.editor.entity.DraftStatus;
import com.cbt.platform.editor.entity.ScenarioDraft;
import com.cbt.platform.editor.entity.ScenarioDraftVersion;
import com.cbt.platform.editor.exception.DraftAlreadyExistsException;
import com.cbt.platform.editor.exception.DraftNotFoundException;
import com.cbt.platform.editor.exception.DraftPublishException;
import com.cbt.platform.editor.exception.VersionNotFoundException;
import com.cbt.platform.editor.mapper.DraftMapper;
import com.cbt.platform.editor.mapper.DraftVersionMapper;
import com.cbt.platform.editor.repository.ScenarioDraftRepository;
import com.cbt.platform.editor.repository.ScenarioDraftVersionRepository;
import com.cbt.platform.user.entity.User;
import com.cbt.platform.user.exception.UserNotFoundException;
import com.cbt.platform.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of ScenarioEditorService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioEditorServiceImpl implements ScenarioEditorService {

    private final ScenarioDraftRepository draftRepository;
    private final ScenarioDraftVersionRepository versionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final DraftMapper draftMapper;
    private final DraftVersionMapper versionMapper;
    private final ScenarioValidationService validationService;

    @Override
    @Transactional(readOnly = true)
    public List<DraftResponse> findAllActive() {
        log.debug("Fetching all active drafts");
        return draftRepository.findAllActive()
                .stream()
                .map(draftMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DraftResponse> findByStatus(DraftStatus status) {
        log.debug("Fetching drafts by status: {}", status);
        return draftRepository.findByStatus(status)
                .stream()
                .map(draftMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DraftResponse> findByCategory(String category) {
        log.debug("Fetching drafts by category: {}", category);
        return draftRepository.findByCategory(category)
                .stream()
                .map(draftMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DraftDetailResponse findById(UUID id) {
        log.debug("Fetching draft by id: {}", id);
        return draftRepository.findActiveById(id)
                .map(draftMapper::toDetailResponse)
                .orElseThrow(() -> new DraftNotFoundException(id));
    }

    @Override
    @Transactional
    public DraftDetailResponse create(CreateDraftRequest request, UUID userId) {
        log.info("Creating draft: {}", request.name());

        if (request.slug() != null && draftRepository.existsBySlug(request.slug())) {
            throw new DraftAlreadyExistsException(request.slug());
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        ScenarioDraft draft = draftMapper.toEntity(request);
        draft.setCreatedBy(user);
        draft.setLastModifiedBy(user);
        draft.setStatus(DraftStatus.DRAFT);

        ValidationResultResponse validation = validationService.validate(request.scenarioJson());
        draft.setIsValid(validation.isValid());
        if (!validation.isValid()) {
            draft.setValidationErrors(convertValidationErrorsToJson(validation.errors()));
        }

        draft = draftRepository.save(draft);

        createVersion(draft, user, "Initial version");

        log.info("Draft created: id={}, name={}", draft.getId(), draft.getName());
        return draftMapper.toDetailResponse(draft);
    }

    @Override
    @Transactional
    public DraftDetailResponse update(UUID id, UpdateDraftRequest request, UUID userId) {
        log.info("Updating draft: {}", id);

        ScenarioDraft draft = draftRepository.findActiveById(id)
                .orElseThrow(() -> new DraftNotFoundException(id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (request.slug() != null && !request.slug().equals(draft.getSlug())) {
            if (draftRepository.existsBySlugExcludingId(request.slug(), id)) {
                throw new DraftAlreadyExistsException(request.slug());
            }
        }

        JsonNode oldScenarioJson = draft.getScenarioJson();
        draftMapper.updateEntity(draft, request);
        draft.setLastModifiedBy(user);

        if (request.scenarioJson() != null && !request.scenarioJson().equals(oldScenarioJson)) {
            ValidationResultResponse validation = validationService.validate(request.scenarioJson());
            draft.setIsValid(validation.isValid());
            if (!validation.isValid()) {
                draft.setValidationErrors(convertValidationErrorsToJson(validation.errors()));
            } else {
                draft.setValidationErrors(null);
            }

            createVersion(draft, user, request.changeDescription());
        }

        draft = draftRepository.save(draft);

        log.info("Draft updated: id={}", draft.getId());
        return draftMapper.toDetailResponse(draft);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting draft: {}", id);

        ScenarioDraft draft = draftRepository.findActiveById(id)
                .orElseThrow(() -> new DraftNotFoundException(id));

        draftRepository.softDelete(id);

        log.info("Draft deleted: id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ValidationResultResponse validateDraft(UUID id) {
        log.debug("Validating draft: {}", id);

        ScenarioDraft draft = draftRepository.findActiveById(id)
                .orElseThrow(() -> new DraftNotFoundException(id));

        return validationService.validate(draft.getScenarioJson());
    }

    @Override
    @Transactional
    public UUID publishDraft(UUID id, PublishDraftRequest request) {
        log.info("Publishing draft: {}", id);

        ScenarioDraft draft = draftRepository.findActiveById(id)
                .orElseThrow(() -> new DraftNotFoundException(id));

        if (!draft.getIsValid()) {
            throw new DraftPublishException("Draft has validation errors");
        }

        if (draft.getSlug() == null || draft.getSlug().isBlank()) {
            throw new DraftPublishException("Draft must have a slug");
        }

        if (courseRepository.existsBySlug(draft.getSlug())) {
            throw new CourseAlreadyExistsException(draft.getSlug());
        }

        Course course = Course.builder()
                .slug(draft.getSlug())
                .name(draft.getName())
                .category(draft.getCategory())
                .scenarioJson(draft.getScenarioJson())
                .version(draft.getVersion())
                .freeSessions(request.freeSessions() != null ? request.freeSessions() : 2)
                .price(request.price())
                .imageUrl(request.imageUrl())
                .estimatedDurationMinutes(request.estimatedDurationMinutes())
                .isActive(true)
                .isPublished(request.isPublished() != null ? request.isPublished() : false)
                .build();

        course = courseRepository.save(course);

        draft.setStatus(DraftStatus.PUBLISHED);
        draft.setPublishedCourse(course);
        draft.setPublishedAt(LocalDateTime.now());
        draftRepository.save(draft);

        log.info("Draft published: draftId={}, courseId={}", id, course.getId());
        return course.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DraftVersionResponse> getDraftVersions(UUID draftId) {
        log.debug("Fetching versions for draft: {}", draftId);

        if (!draftRepository.findActiveById(draftId).isPresent()) {
            throw new DraftNotFoundException(draftId);
        }

        return versionRepository.findByDraftIdOrderByVersionNumberDesc(draftId)
                .stream()
                .map(versionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DraftVersionResponse getDraftVersion(UUID draftId, Integer versionNumber) {
        log.debug("Fetching version {} for draft: {}", versionNumber, draftId);

        return versionRepository.findByDraftIdAndVersionNumber(draftId, versionNumber)
                .map(versionMapper::toResponse)
                .orElseThrow(() -> new VersionNotFoundException(draftId, versionNumber));
    }

    @Override
    @Transactional
    public DraftDetailResponse restoreVersion(UUID draftId, Integer versionNumber, UUID userId) {
        log.info("Restoring draft {} to version {}", draftId, versionNumber);

        ScenarioDraft draft = draftRepository.findActiveById(draftId)
                .orElseThrow(() -> new DraftNotFoundException(draftId));

        ScenarioDraftVersion version = versionRepository.findByDraftIdAndVersionNumber(draftId, versionNumber)
                .orElseThrow(() -> new VersionNotFoundException(draftId, versionNumber));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        draft.setScenarioJson(version.getScenarioJson());
        draft.setLastModifiedBy(user);

        ValidationResultResponse validation = validationService.validate(version.getScenarioJson());
        draft.setIsValid(validation.isValid());
        if (!validation.isValid()) {
            draft.setValidationErrors(convertValidationErrorsToJson(validation.errors()));
        } else {
            draft.setValidationErrors(null);
        }

        createVersion(draft, user, "Restored from version " + versionNumber);

        draft = draftRepository.save(draft);

        log.info("Draft restored: draftId={}, fromVersion={}", draftId, versionNumber);
        return draftMapper.toDetailResponse(draft);
    }

    private void createVersion(ScenarioDraft draft, User user, String changeDescription) {
        Integer nextVersionNumber = versionRepository.getNextVersionNumber(draft.getId());

        ScenarioDraftVersion version = ScenarioDraftVersion.builder()
                .draft(draft)
                .versionNumber(nextVersionNumber)
                .scenarioJson(draft.getScenarioJson())
                .changeDescription(changeDescription)
                .createdBy(user)
                .build();

        versionRepository.save(version);
        log.debug("Created version {} for draft {}", nextVersionNumber, draft.getId());
    }

    private JsonNode convertValidationErrorsToJson(List<ValidationResultResponse.ValidationError> errors) {
        return null;
    }
}
