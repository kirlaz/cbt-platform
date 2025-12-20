package com.cbt.platform.progress.controller;

import com.cbt.platform.progress.dto.*;
import com.cbt.platform.progress.service.ProgressService;
import com.cbt.platform.user.entity.User;
import com.cbt.platform.user.exception.UserNotFoundException;
import com.cbt.platform.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for user progress management
 */
@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@Tag(name = "Progress", description = "User course progress management endpoints")
public class ProgressController {

    private final ProgressService progressService;
    private final UserRepository userRepository;

    @PostMapping("/start")
    @Operation(summary = "Start a course", description = "Initialize progress tracking for a course")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Course started successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "409", description = "Course already started")
    })
    public ResponseEntity<ProgressResponse> startCourse(
            Authentication authentication,
            @Valid @RequestBody StartCourseRequest request) {
        UUID userId = getUserIdFromAuth(authentication);
        ProgressResponse progress = progressService.startCourse(userId, request);
        URI location = URI.create("/api/progress/" + progress.id());
        return ResponseEntity.created(location).body(progress);
    }

    @GetMapping("/my")
    @Operation(summary = "Get my progress", description = "Get all progress records for authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progress retrieved successfully")
    })
    public ResponseEntity<List<ProgressResponse>> getMyProgress(Authentication authentication) {
        UUID userId = getUserIdFromAuth(authentication);
        List<ProgressResponse> progress = progressService.getAllProgressForUser(userId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/my/active")
    @Operation(summary = "Get my active courses", description = "Get active (incomplete) courses for authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Active progress retrieved successfully")
    })
    public ResponseEntity<List<ProgressResponse>> getMyActiveProgress(Authentication authentication) {
        UUID userId = getUserIdFromAuth(authentication);
        List<ProgressResponse> progress = progressService.getActiveProgressForUser(userId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get progress in a course", description = "Get user's progress in a specific course")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progress found"),
            @ApiResponse(responseCode = "404", description = "Progress not found")
    })
    public ResponseEntity<ProgressResponse> getProgressInCourse(
            Authentication authentication,
            @PathVariable UUID courseId) {
        UUID userId = getUserIdFromAuth(authentication);
        ProgressResponse progress = progressService.getProgress(userId, courseId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/{progressId}")
    @Operation(summary = "Get progress by ID", description = "Get progress details by progress ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progress found"),
            @ApiResponse(responseCode = "404", description = "Progress not found")
    })
    public ResponseEntity<ProgressResponse> getProgressById(@PathVariable UUID progressId) {
        ProgressResponse progress = progressService.getProgressById(progressId);
        return ResponseEntity.ok(progress);
    }

    @PutMapping("/course/{courseId}")
    @Operation(summary = "Update progress", description = "Update user's progress in a course (position, completion)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progress updated successfully"),
            @ApiResponse(responseCode = "404", description = "Progress not found")
    })
    public ResponseEntity<ProgressResponse> updateProgress(
            Authentication authentication,
            @PathVariable UUID courseId,
            @Valid @RequestBody UpdateProgressRequest request) {
        UUID userId = getUserIdFromAuth(authentication);
        ProgressResponse progress = progressService.updateProgress(userId, courseId, request);
        return ResponseEntity.ok(progress);
    }

    @PutMapping("/course/{courseId}/user-data")
    @Operation(
            summary = "Update user data",
            description = "Update user data accumulated during the course (merge or replace)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User data updated successfully"),
            @ApiResponse(responseCode = "404", description = "Progress not found")
    })
    public ResponseEntity<ProgressResponse> updateUserData(
            Authentication authentication,
            @PathVariable UUID courseId,
            @Valid @RequestBody UpdateUserDataRequest request) {
        UUID userId = getUserIdFromAuth(authentication);
        ProgressResponse progress = progressService.updateUserData(userId, courseId, request);
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/course/{courseId}/complete-session")
    @Operation(summary = "Complete a session", description = "Mark a session as completed")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session marked as completed"),
            @ApiResponse(responseCode = "404", description = "Progress not found")
    })
    public ResponseEntity<ProgressResponse> completeSession(
            Authentication authentication,
            @PathVariable UUID courseId,
            @Valid @RequestBody CompleteSessionRequest request) {
        UUID userId = getUserIdFromAuth(authentication);
        ProgressResponse progress = progressService.completeSession(userId, courseId, request);
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/course/{courseId}/complete-block/{sessionId}/{blockId}")
    @Operation(summary = "Complete a block", description = "Mark a specific block as completed")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Block marked as completed"),
            @ApiResponse(responseCode = "404", description = "Progress not found")
    })
    public ResponseEntity<ProgressResponse> completeBlock(
            Authentication authentication,
            @PathVariable UUID courseId,
            @PathVariable String sessionId,
            @PathVariable String blockId) {
        UUID userId = getUserIdFromAuth(authentication);
        ProgressResponse progress = progressService.completeBlock(userId, courseId, sessionId, blockId);
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/course/{courseId}/complete")
    @Operation(summary = "Complete course", description = "Mark entire course as completed")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course marked as completed"),
            @ApiResponse(responseCode = "404", description = "Progress not found")
    })
    public ResponseEntity<ProgressResponse> completeCourse(
            Authentication authentication,
            @PathVariable UUID courseId) {
        UUID userId = getUserIdFromAuth(authentication);
        ProgressResponse progress = progressService.completeCourse(userId, courseId);
        return ResponseEntity.ok(progress);
    }

    @DeleteMapping("/course/{courseId}")
    @Operation(summary = "Delete progress", description = "Delete progress in a course (for testing/reset)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Progress deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Progress not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProgress(
            Authentication authentication,
            @PathVariable UUID courseId) {
        UUID userId = getUserIdFromAuth(authentication);
        progressService.deleteProgress(userId, courseId);
    }

    /**
     * Extract user ID from Authentication (Spring Security)
     * Gets email from JWT token (subject) and looks up user in database
     *
     * NOTE: This implementation requires a DB query per request.
     * For better performance, consider adding userId to JWT claims in future.
     */
    private UUID getUserIdFromAuth(Authentication authentication) {
        String email = authentication.getName(); // Email is stored in JWT subject
        User user = userRepository.findActiveByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return user.getId();
    }
}
