package com.cbt.platform.engine.controller;

import com.cbt.platform.engine.dto.BlockInputRequest;
import com.cbt.platform.engine.dto.BlockResult;
import com.cbt.platform.engine.service.CourseEngine;
import com.cbt.platform.user.entity.User;
import com.cbt.platform.user.exception.UserNotFoundException;
import com.cbt.platform.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for course session operations
 * Handles block navigation and user interaction with course content
 */
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Tag(name = "Sessions", description = "Course session and block interaction endpoints")
public class SessionController {

    private final CourseEngine courseEngine;
    private final UserRepository userRepository;

    @GetMapping("/courses/{courseId}/current-block")
    @Operation(summary = "Get current block", description = "Get the current block for authenticated user in course")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Current block retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Course or progress not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<BlockResult> getCurrentBlock(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getUserId(userDetails);
        BlockResult result = courseEngine.getCurrentBlock(userId, courseId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/courses/{courseId}/submit-block")
    @Operation(summary = "Submit block input", description = "Submit user input for current block")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Block input processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or validation error"),
            @ApiResponse(responseCode = "404", description = "Course, progress, or block not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<BlockResult> submitBlockInput(
            @PathVariable UUID courseId,
            @Valid @RequestBody BlockInputRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getUserId(userDetails);
        BlockResult result = courseEngine.processBlockInput(userId, courseId, request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/courses/{courseId}/next-block")
    @Operation(summary = "Navigate to next block", description = "Move to the next block in the session")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Navigated to next block successfully"),
            @ApiResponse(responseCode = "404", description = "Course or progress not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<BlockResult> nextBlock(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getUserId(userDetails);
        BlockResult result = courseEngine.nextBlock(userId, courseId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get user ID from UserDetails (email)
     * UserDetails.getUsername() returns email, not UUID
     */
    private UUID getUserId(UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findActiveByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return user.getId();
    }
}
