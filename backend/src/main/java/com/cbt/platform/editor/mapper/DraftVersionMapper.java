package com.cbt.platform.editor.mapper;

import com.cbt.platform.editor.dto.DraftVersionResponse;
import com.cbt.platform.editor.entity.ScenarioDraftVersion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for ScenarioDraftVersion entity and DTOs
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DraftVersionMapper {

    /**
     * Convert ScenarioDraftVersion entity to DraftVersionResponse DTO
     */
    @Mapping(target = "draftId", source = "draft.id")
    @Mapping(target = "createdByUserId", source = "createdBy.id")
    @Mapping(target = "createdByName", source = "createdBy.email")
    DraftVersionResponse toResponse(ScenarioDraftVersion version);
}
