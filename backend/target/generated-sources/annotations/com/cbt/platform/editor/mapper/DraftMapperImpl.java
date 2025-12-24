package com.cbt.platform.editor.mapper;

import com.cbt.platform.course.entity.Course;
import com.cbt.platform.editor.dto.CreateDraftRequest;
import com.cbt.platform.editor.dto.DraftDetailResponse;
import com.cbt.platform.editor.dto.DraftResponse;
import com.cbt.platform.editor.dto.UpdateDraftRequest;
import com.cbt.platform.editor.entity.DraftStatus;
import com.cbt.platform.editor.entity.ScenarioDraft;
import com.cbt.platform.user.entity.User;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-23T22:05:05+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class DraftMapperImpl implements DraftMapper {

    @Override
    public DraftResponse toResponse(ScenarioDraft draft) {
        if ( draft == null ) {
            return null;
        }

        String createdByName = null;
        String lastModifiedByName = null;
        UUID id = null;
        String name = null;
        String slug = null;
        String category = null;
        String version = null;
        DraftStatus status = null;
        boolean isValid = false;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;
        LocalDateTime publishedAt = null;

        createdByName = draftCreatedByEmail( draft );
        lastModifiedByName = draftLastModifiedByEmail( draft );
        id = draft.getId();
        name = draft.getName();
        slug = draft.getSlug();
        category = draft.getCategory();
        version = draft.getVersion();
        status = draft.getStatus();
        if ( draft.getIsValid() != null ) {
            isValid = draft.getIsValid();
        }
        createdAt = draft.getCreatedAt();
        updatedAt = draft.getUpdatedAt();
        publishedAt = draft.getPublishedAt();

        DraftResponse draftResponse = new DraftResponse( id, name, slug, category, version, status, isValid, createdByName, lastModifiedByName, createdAt, updatedAt, publishedAt );

        return draftResponse;
    }

    @Override
    public DraftDetailResponse toDetailResponse(ScenarioDraft draft) {
        if ( draft == null ) {
            return null;
        }

        UUID createdByUserId = null;
        String createdByName = null;
        UUID lastModifiedByUserId = null;
        String lastModifiedByName = null;
        UUID publishedCourseId = null;
        UUID id = null;
        String name = null;
        String slug = null;
        String category = null;
        String version = null;
        JsonNode scenarioJson = null;
        DraftStatus status = null;
        boolean isValid = false;
        JsonNode validationErrors = null;
        LocalDateTime publishedAt = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        createdByUserId = draftCreatedById( draft );
        createdByName = draftCreatedByEmail( draft );
        lastModifiedByUserId = draftLastModifiedById( draft );
        lastModifiedByName = draftLastModifiedByEmail( draft );
        publishedCourseId = draftPublishedCourseId( draft );
        id = draft.getId();
        name = draft.getName();
        slug = draft.getSlug();
        category = draft.getCategory();
        version = draft.getVersion();
        scenarioJson = draft.getScenarioJson();
        status = draft.getStatus();
        if ( draft.getIsValid() != null ) {
            isValid = draft.getIsValid();
        }
        validationErrors = draft.getValidationErrors();
        publishedAt = draft.getPublishedAt();
        createdAt = draft.getCreatedAt();
        updatedAt = draft.getUpdatedAt();

        DraftDetailResponse draftDetailResponse = new DraftDetailResponse( id, name, slug, category, version, scenarioJson, status, isValid, validationErrors, createdByUserId, createdByName, lastModifiedByUserId, lastModifiedByName, publishedCourseId, publishedAt, createdAt, updatedAt );

        return draftDetailResponse;
    }

    @Override
    public ScenarioDraft toEntity(CreateDraftRequest request) {
        if ( request == null ) {
            return null;
        }

        ScenarioDraft.ScenarioDraftBuilder scenarioDraft = ScenarioDraft.builder();

        scenarioDraft.name( request.name() );
        scenarioDraft.slug( request.slug() );
        scenarioDraft.category( request.category() );
        scenarioDraft.version( request.version() );
        scenarioDraft.scenarioJson( request.scenarioJson() );

        return scenarioDraft.build();
    }

    @Override
    public void updateEntity(ScenarioDraft draft, UpdateDraftRequest request) {
        if ( request == null ) {
            return;
        }

        if ( request.name() != null ) {
            draft.setName( request.name() );
        }
        if ( request.slug() != null ) {
            draft.setSlug( request.slug() );
        }
        if ( request.category() != null ) {
            draft.setCategory( request.category() );
        }
        if ( request.version() != null ) {
            draft.setVersion( request.version() );
        }
        if ( request.scenarioJson() != null ) {
            draft.setScenarioJson( request.scenarioJson() );
        }
    }

    private String draftCreatedByEmail(ScenarioDraft scenarioDraft) {
        if ( scenarioDraft == null ) {
            return null;
        }
        User createdBy = scenarioDraft.getCreatedBy();
        if ( createdBy == null ) {
            return null;
        }
        String email = createdBy.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }

    private String draftLastModifiedByEmail(ScenarioDraft scenarioDraft) {
        if ( scenarioDraft == null ) {
            return null;
        }
        User lastModifiedBy = scenarioDraft.getLastModifiedBy();
        if ( lastModifiedBy == null ) {
            return null;
        }
        String email = lastModifiedBy.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }

    private UUID draftCreatedById(ScenarioDraft scenarioDraft) {
        if ( scenarioDraft == null ) {
            return null;
        }
        User createdBy = scenarioDraft.getCreatedBy();
        if ( createdBy == null ) {
            return null;
        }
        UUID id = createdBy.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID draftLastModifiedById(ScenarioDraft scenarioDraft) {
        if ( scenarioDraft == null ) {
            return null;
        }
        User lastModifiedBy = scenarioDraft.getLastModifiedBy();
        if ( lastModifiedBy == null ) {
            return null;
        }
        UUID id = lastModifiedBy.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID draftPublishedCourseId(ScenarioDraft scenarioDraft) {
        if ( scenarioDraft == null ) {
            return null;
        }
        Course publishedCourse = scenarioDraft.getPublishedCourse();
        if ( publishedCourse == null ) {
            return null;
        }
        UUID id = publishedCourse.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
