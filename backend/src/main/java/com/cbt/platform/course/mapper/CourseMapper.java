package com.cbt.platform.course.mapper;

import com.cbt.platform.course.dto.*;
import com.cbt.platform.course.entity.Course;
import org.mapstruct.*;

/**
 * MapStruct mapper for Course entity and DTOs
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseMapper {

    /**
     * Convert Course entity to CourseResponse DTO
     */
    CourseResponse toResponse(Course course);

    /**
     * Convert Course entity to CourseDetailResponse DTO
     */
    CourseDetailResponse toDetailResponse(Course course);

    /**
     * Convert CreateCourseRequest to Course entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Course toEntity(CreateCourseRequest request);

    /**
     * Update existing Course entity from UpdateCourseRequest
     * Only updates non-null fields
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntity(@MappingTarget Course course, UpdateCourseRequest request);
}
