package com.cbt.platform.editor.mapper;

import com.cbt.platform.editor.dto.DraftVersionResponse;
import com.cbt.platform.editor.entity.ScenarioDraft;
import com.cbt.platform.editor.entity.ScenarioDraftVersion;
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
public class DraftVersionMapperImpl implements DraftVersionMapper {

    @Override
    public DraftVersionResponse toResponse(ScenarioDraftVersion version) {
        if ( version == null ) {
            return null;
        }

        UUID draftId = null;
        UUID createdByUserId = null;
        String createdByName = null;
        UUID id = null;
        Integer versionNumber = null;
        JsonNode scenarioJson = null;
        String changeDescription = null;
        LocalDateTime createdAt = null;

        draftId = versionDraftId( version );
        createdByUserId = versionCreatedById( version );
        createdByName = versionCreatedByEmail( version );
        id = version.getId();
        versionNumber = version.getVersionNumber();
        scenarioJson = version.getScenarioJson();
        changeDescription = version.getChangeDescription();
        createdAt = version.getCreatedAt();

        DraftVersionResponse draftVersionResponse = new DraftVersionResponse( id, draftId, versionNumber, scenarioJson, changeDescription, createdByUserId, createdByName, createdAt );

        return draftVersionResponse;
    }

    private UUID versionDraftId(ScenarioDraftVersion scenarioDraftVersion) {
        if ( scenarioDraftVersion == null ) {
            return null;
        }
        ScenarioDraft draft = scenarioDraftVersion.getDraft();
        if ( draft == null ) {
            return null;
        }
        UUID id = draft.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID versionCreatedById(ScenarioDraftVersion scenarioDraftVersion) {
        if ( scenarioDraftVersion == null ) {
            return null;
        }
        User createdBy = scenarioDraftVersion.getCreatedBy();
        if ( createdBy == null ) {
            return null;
        }
        UUID id = createdBy.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String versionCreatedByEmail(ScenarioDraftVersion scenarioDraftVersion) {
        if ( scenarioDraftVersion == null ) {
            return null;
        }
        User createdBy = scenarioDraftVersion.getCreatedBy();
        if ( createdBy == null ) {
            return null;
        }
        String email = createdBy.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }
}
