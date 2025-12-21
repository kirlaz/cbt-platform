package com.cbt.platform.editor.mapper;

import com.cbt.platform.editor.dto.BlockTemplateResponse;
import com.cbt.platform.editor.dto.CreateBlockTemplateRequest;
import com.cbt.platform.editor.dto.UpdateBlockTemplateRequest;
import com.cbt.platform.editor.entity.BlockTemplate;
import org.mapstruct.*;

/**
 * MapStruct mapper for BlockTemplate entity and DTOs
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BlockTemplateMapper {

    /**
     * Convert BlockTemplate entity to BlockTemplateResponse DTO
     */
    BlockTemplateResponse toResponse(BlockTemplate template);

    /**
     * Convert CreateBlockTemplateRequest to BlockTemplate entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isSystem", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "usageCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    BlockTemplate toEntity(CreateBlockTemplateRequest request);

    /**
     * Update existing BlockTemplate entity from UpdateBlockTemplateRequest
     * Only updates non-null fields
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isSystem", ignore = true)
    @Mapping(target = "usageCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntity(@MappingTarget BlockTemplate template, UpdateBlockTemplateRequest request);
}
