package com.cbt.platform.course.controller;

import com.cbt.platform.course.dto.*;
import com.cbt.platform.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for course management operations
 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course management endpoints")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @Operation(summary = "Get all active and published courses", description = "Retrieve all courses available to users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    })
    public ResponseEntity<List<CourseResponse>> getAllPublished() {
        List<CourseResponse> courses = courseService.findAllActiveAndPublished();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all active courses (admin only)", description = "Retrieve all active courses including unpublished")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<CourseResponse>> getAllActive() {
        List<CourseResponse> courses = courseService.findAllActive();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get courses by category", description = "Retrieve courses filtered by category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    })
    public ResponseEntity<List<CourseResponse>> getByCategory(@PathVariable String category) {
        List<CourseResponse> courses = courseService.findByCategory(category);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get course by slug", description = "Retrieve course details including scenario JSON")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course found"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<CourseDetailResponse> getBySlug(@PathVariable String slug) {
        CourseDetailResponse course = courseService.findBySlug(slug);
        return ResponseEntity.ok(course);
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get course by ID (admin only)", description = "Retrieve course details by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course found"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<CourseDetailResponse> getById(@PathVariable UUID id) {
        CourseDetailResponse course = courseService.findById(id);
        return ResponseEntity.ok(course);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create course manually (admin only)", description = "Create a new course with provided data")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Course created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Course already exists")
    })
    public ResponseEntity<CourseDetailResponse> create(@Valid @RequestBody CreateCourseRequest request) {
        CourseDetailResponse course = courseService.create(request);
        URI location = URI.create("/api/courses/" + course.slug());
        return ResponseEntity.created(location).body(course);
    }

    @PostMapping("/load-scenario")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Load course from scenario JSON file (admin only)",
            description = "Load a course from a scenario file in resources/scenarios/{slug}/scenario_{version}.json"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Course loaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid scenario path or JSON format"),
            @ApiResponse(responseCode = "500", description = "Failed to load scenario file")
    })
    public ResponseEntity<CourseDetailResponse> loadFromScenario(@Valid @RequestBody LoadScenarioRequest request) {
        CourseDetailResponse course = courseService.loadFromScenario(request.scenarioPath());
        URI location = URI.create("/api/courses/" + course.slug());
        return ResponseEntity.created(location).body(course);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update course (admin only)", description = "Update course details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course updated successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<CourseDetailResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCourseRequest request) {
        CourseDetailResponse course = courseService.update(id, request);
        return ResponseEntity.ok(course);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete course (admin only)", description = "Soft delete a course")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Course deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        courseService.delete(id);
    }
}
