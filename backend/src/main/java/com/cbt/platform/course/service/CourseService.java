package com.cbt.platform.course.service;

import com.cbt.platform.course.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for course management operations
 */
public interface CourseService {

    /**
     * Find all active and published courses
     *
     * @return list of active courses
     */
    List<CourseResponse> findAllActiveAndPublished();

    /**
     * Find all active courses (admin only)
     *
     * @return list of all active courses
     */
    List<CourseResponse> findAllActive();

    /**
     * Find courses by category
     *
     * @param category course category
     * @return list of courses in category
     */
    List<CourseResponse> findByCategory(String category);

    /**
     * Find course by slug
     *
     * @param slug course slug
     * @return course detail
     */
    CourseDetailResponse findBySlug(String slug);

    /**
     * Find course by ID
     *
     * @param id course ID
     * @return course detail
     */
    CourseDetailResponse findById(UUID id);

    /**
     * Create a new course manually
     *
     * @param request course creation data
     * @return created course
     */
    CourseDetailResponse create(CreateCourseRequest request);

    /**
     * Load course from scenario JSON file in resources
     *
     * @param scenarioPath path to scenario file (e.g., "scenarios/anxiety/scenario_1.0.1.json")
     * @return created course
     */
    CourseDetailResponse loadFromScenario(String scenarioPath);

    /**
     * Update existing course
     *
     * @param id      course ID
     * @param request update data
     * @return updated course
     */
    CourseDetailResponse update(UUID id, UpdateCourseRequest request);

    /**
     * Soft delete course
     *
     * @param id course ID
     */
    void delete(UUID id);

    /**
     * Check if course exists by slug
     *
     * @param slug course slug
     * @return true if exists
     */
    boolean existsBySlug(String slug);
}
