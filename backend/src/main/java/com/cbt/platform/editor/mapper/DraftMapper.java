package com.cbt.platform.editor.mapper;

import com.cbt.platform.editor.dto.*;
import com.cbt.platform.editor.entity.ScenarioDraft;
import org.mapstruct.*;

/**
 * MapStruct mapper for ScenarioDraft entity and DTOs
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DraftMapper {

    /**
     * Convert ScenarioDraft entity to DraftResponse DTO
     */
    @Mapping(target = "createdByName", source = "createdBy.email")
    @Mapping(target = "lastModifiedByName", source = "lastModifiedBy.email")
    DraftResponse toResponse(ScenarioDraft draft);

    /**
     * Convert ScenarioDraft entity to DraftDetailResponse DTO
     */
    @Mapping(target = "createdByUserId", source = "createdBy.id")
    @Mapping(target = "createdByName", source = "createdBy.email")
    @Mapping(target = "lastModifiedByUserId", source = "lastModifiedBy.id")
    @Mapping(target = "lastModifiedByName", source = "lastModifiedBy.email")
    @Mapping(target = "publishedCourseId", source = "publishedCourse.id")
    DraftDetailResponse toDetailResponse(ScenarioDraft draft);

    /**
     * Convert CreateDraftRequest to ScenarioDraft entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "isValid", ignore = true)
    @Mapping(target = "validationErrors", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "publishedCourse", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    ScenarioDraft toEntity(CreateDraftRequest request);

    /**
     * Update existing ScenarioDraft entity from UpdateDraftRequest
     * Only updates non-null fields
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "isValid", ignore = true)
    @Mapping(target = "validationErrors", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "publishedCourse", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntity(@MappingTarget ScenarioDraft draft, UpdateDraftRequest request);
}
