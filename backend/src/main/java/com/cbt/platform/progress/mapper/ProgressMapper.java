package com.cbt.platform.progress.mapper;

import com.cbt.platform.progress.dto.ProgressResponse;
import com.cbt.platform.progress.dto.UpdateProgressRequest;
import com.cbt.platform.progress.entity.UserProgress;
import org.mapstruct.*;

/**
 * MapStruct mapper for UserProgress entity and DTOs
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProgressMapper {

    /**
     * Convert UserProgress entity to ProgressResponse DTO
     */
    ProgressResponse toResponse(UserProgress progress);

    /**
     * Update existing UserProgress entity from UpdateProgressRequest
     * Only updates non-null fields
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "courseId", ignore = true)
    @Mapping(target = "completedSessions", ignore = true)
    @Mapping(target = "completedBlocks", ignore = true)
    @Mapping(target = "isCompleted", ignore = true)
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "lastActivityAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget UserProgress progress, UpdateProgressRequest request);
}
