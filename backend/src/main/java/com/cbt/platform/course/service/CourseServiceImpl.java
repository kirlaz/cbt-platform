package com.cbt.platform.course.service;

import com.cbt.platform.course.dto.*;
import com.cbt.platform.course.entity.Course;
import com.cbt.platform.course.exception.CourseAlreadyExistsException;
import com.cbt.platform.course.exception.CourseNotFoundException;
import com.cbt.platform.course.exception.ScenarioLoadException;
import com.cbt.platform.course.mapper.CourseMapper;
import com.cbt.platform.course.repository.CourseRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of CourseService for course management operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final ObjectMapper objectMapper;

    private static final Pattern SCENARIO_PATH_PATTERN = Pattern.compile("scenarios/([a-z0-9-]+)/scenario_(\\d+\\.\\d+\\.\\d+)\\.json");

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> findAllActiveAndPublished() {
        log.debug("Fetching all active and published courses");
        return courseRepository.findAllActiveAndPublished()
                .stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> findAllActive() {
        log.debug("Fetching all active courses");
        return courseRepository.findAllActive()
                .stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> findByCategory(String category) {
        log.debug("Fetching courses by category: {}", category);
        return courseRepository.findByCategory(category)
                .stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDetailResponse findBySlug(String slug) {
        log.debug("Fetching course by slug: {}", slug);
        return courseRepository.findActiveBySlug(slug)
                .map(courseMapper::toDetailResponse)
                .orElseThrow(() -> new CourseNotFoundException(slug));
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDetailResponse findById(UUID id) {
        log.debug("Fetching course by id: {}", id);
        return courseRepository.findById(id)
                .map(courseMapper::toDetailResponse)
                .orElseThrow(() -> new CourseNotFoundException(id));
    }

    @Override
    @Transactional
    public CourseDetailResponse create(CreateCourseRequest request) {
        log.info("Creating course: {}", request.slug());

        if (courseRepository.existsBySlug(request.slug())) {
            throw new CourseAlreadyExistsException(request.slug());
        }

        Course course = courseMapper.toEntity(request);
        course = courseRepository.save(course);

        log.info("Course created: id={}, slug={}", course.getId(), course.getSlug());
        return courseMapper.toDetailResponse(course);
    }

    @Override
    @Transactional
    public CourseDetailResponse loadFromScenario(String scenarioPath) {
        log.info("Loading course from scenario: {}", scenarioPath);

        // Parse scenario path to extract slug and version
        Matcher matcher = SCENARIO_PATH_PATTERN.matcher(scenarioPath);
        if (!matcher.matches()) {
            throw new ScenarioLoadException(
                    "Invalid scenario path format. Expected: scenarios/{slug}/scenario_{version}.json"
            );
        }

        String slug = matcher.group(1);
        String version = matcher.group(2);

        // Check if course already exists
        if (courseRepository.existsBySlug(slug)) {
            log.warn("Course already exists with slug: {}, updating scenario", slug);
            Course existingCourse = courseRepository.findBySlug(slug)
                    .orElseThrow(() -> new CourseNotFoundException(slug));

            JsonNode scenarioJson = loadScenarioJsonFromFile(scenarioPath);
            existingCourse.setScenarioJson(scenarioJson);
            existingCourse.setVersion(version);

            Course updated = courseRepository.save(existingCourse);
            log.info("Course scenario updated: id={}, slug={}, version={}", updated.getId(), updated.getSlug(), updated.getVersion());
            return courseMapper.toDetailResponse(updated);
        }

        // Load scenario JSON from classpath
        JsonNode scenarioJson = loadScenarioJsonFromFile(scenarioPath);

        // Extract metadata from scenario JSON
        JsonNode meta = scenarioJson.get("meta");
        if (meta == null) {
            throw new ScenarioLoadException("Scenario JSON missing 'meta' field");
        }

        String name = meta.has("name") ? meta.get("name").asText() : slug;
        String description = meta.has("description") ? meta.get("description").asText() : "";

        // Create course entity
        Course course = Course.builder()
                .slug(slug)
                .name(name)
                .description(description)
                .scenarioJson(scenarioJson)
                .version(version)
                .freeSessions(2) // Default from project spec
                .category(slug.split("-")[0]) // First part of slug as category
                .isActive(true)
                .isPublished(false) // Not published by default, admin must publish
                .build();

        course = courseRepository.save(course);
        log.info("Course loaded from scenario: id={}, slug={}, version={}", course.getId(), course.getSlug(), course.getVersion());

        return courseMapper.toDetailResponse(course);
    }

    @Override
    @Transactional
    public CourseDetailResponse update(UUID id, UpdateCourseRequest request) {
        log.info("Updating course: id={}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));

        courseMapper.updateEntity(course, request);
        course = courseRepository.save(course);

        log.info("Course updated: id={}, slug={}", course.getId(), course.getSlug());
        return courseMapper.toDetailResponse(course);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Soft deleting course: id={}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));

        courseRepository.softDelete(id);
        log.info("Course soft deleted: id={}, slug={}", id, course.getSlug());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySlug(String slug) {
        return courseRepository.existsBySlug(slug);
    }

    /**
     * Load and parse scenario JSON from classpath resource
     */
    private JsonNode loadScenarioJsonFromFile(String scenarioPath) {
        try {
            ClassPathResource resource = new ClassPathResource(scenarioPath);
            if (!resource.exists()) {
                throw new ScenarioLoadException("Scenario file not found: " + scenarioPath);
            }

            try (InputStream inputStream = resource.getInputStream()) {
                return objectMapper.readTree(inputStream);
            }
        } catch (IOException e) {
            log.error("Failed to load scenario from: {}", scenarioPath, e);
            throw new ScenarioLoadException(scenarioPath, e);
        }
    }
}
