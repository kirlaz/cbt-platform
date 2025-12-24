package com.cbt.platform.course.mapper;

import com.cbt.platform.course.dto.CourseDetailResponse;
import com.cbt.platform.course.dto.CourseResponse;
import com.cbt.platform.course.dto.CreateCourseRequest;
import com.cbt.platform.course.dto.UpdateCourseRequest;
import com.cbt.platform.course.entity.Course;
import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
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
public class CourseMapperImpl implements CourseMapper {

    @Override
    public CourseResponse toResponse(Course course) {
        if ( course == null ) {
            return null;
        }

        UUID id = null;
        String slug = null;
        String name = null;
        String description = null;
        String version = null;
        Integer freeSessions = null;
        BigDecimal price = null;
        String imageUrl = null;
        Integer estimatedDurationMinutes = null;
        String category = null;
        boolean isActive = false;
        boolean isPublished = false;
        LocalDateTime createdAt = null;

        id = course.getId();
        slug = course.getSlug();
        name = course.getName();
        description = course.getDescription();
        version = course.getVersion();
        freeSessions = course.getFreeSessions();
        price = course.getPrice();
        imageUrl = course.getImageUrl();
        estimatedDurationMinutes = course.getEstimatedDurationMinutes();
        category = course.getCategory();
        if ( course.getIsActive() != null ) {
            isActive = course.getIsActive();
        }
        if ( course.getIsPublished() != null ) {
            isPublished = course.getIsPublished();
        }
        createdAt = course.getCreatedAt();

        CourseResponse courseResponse = new CourseResponse( id, slug, name, description, version, freeSessions, price, imageUrl, estimatedDurationMinutes, category, isActive, isPublished, createdAt );

        return courseResponse;
    }

    @Override
    public CourseDetailResponse toDetailResponse(Course course) {
        if ( course == null ) {
            return null;
        }

        UUID id = null;
        String slug = null;
        String name = null;
        String description = null;
        JsonNode scenarioJson = null;
        String version = null;
        Integer freeSessions = null;
        BigDecimal price = null;
        String imageUrl = null;
        Integer estimatedDurationMinutes = null;
        String category = null;
        boolean isActive = false;
        boolean isPublished = false;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = course.getId();
        slug = course.getSlug();
        name = course.getName();
        description = course.getDescription();
        scenarioJson = course.getScenarioJson();
        version = course.getVersion();
        freeSessions = course.getFreeSessions();
        price = course.getPrice();
        imageUrl = course.getImageUrl();
        estimatedDurationMinutes = course.getEstimatedDurationMinutes();
        category = course.getCategory();
        if ( course.getIsActive() != null ) {
            isActive = course.getIsActive();
        }
        if ( course.getIsPublished() != null ) {
            isPublished = course.getIsPublished();
        }
        createdAt = course.getCreatedAt();
        updatedAt = course.getUpdatedAt();

        CourseDetailResponse courseDetailResponse = new CourseDetailResponse( id, slug, name, description, scenarioJson, version, freeSessions, price, imageUrl, estimatedDurationMinutes, category, isActive, isPublished, createdAt, updatedAt );

        return courseDetailResponse;
    }

    @Override
    public Course toEntity(CreateCourseRequest request) {
        if ( request == null ) {
            return null;
        }

        Course.CourseBuilder course = Course.builder();

        course.slug( request.slug() );
        course.name( request.name() );
        course.description( request.description() );
        course.scenarioJson( request.scenarioJson() );
        course.version( request.version() );
        course.freeSessions( request.freeSessions() );
        course.price( request.price() );
        course.imageUrl( request.imageUrl() );
        course.estimatedDurationMinutes( request.estimatedDurationMinutes() );
        course.category( request.category() );
        course.isPublished( request.isPublished() );

        return course.build();
    }

    @Override
    public void updateEntity(Course course, UpdateCourseRequest request) {
        if ( request == null ) {
            return;
        }

        if ( request.name() != null ) {
            course.setName( request.name() );
        }
        if ( request.description() != null ) {
            course.setDescription( request.description() );
        }
        if ( request.scenarioJson() != null ) {
            course.setScenarioJson( request.scenarioJson() );
        }
        if ( request.version() != null ) {
            course.setVersion( request.version() );
        }
        if ( request.freeSessions() != null ) {
            course.setFreeSessions( request.freeSessions() );
        }
        if ( request.price() != null ) {
            course.setPrice( request.price() );
        }
        if ( request.imageUrl() != null ) {
            course.setImageUrl( request.imageUrl() );
        }
        if ( request.estimatedDurationMinutes() != null ) {
            course.setEstimatedDurationMinutes( request.estimatedDurationMinutes() );
        }
        if ( request.category() != null ) {
            course.setCategory( request.category() );
        }
        if ( request.isActive() != null ) {
            course.setIsActive( request.isActive() );
        }
        if ( request.isPublished() != null ) {
            course.setIsPublished( request.isPublished() );
        }
    }
}
