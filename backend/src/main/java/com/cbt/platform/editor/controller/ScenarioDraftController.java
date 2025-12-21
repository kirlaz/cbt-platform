package com.cbt.platform.editor.controller;

import com.cbt.platform.editor.dto.*;
import com.cbt.platform.editor.entity.DraftStatus;
import com.cbt.platform.editor.service.ScenarioEditorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for scenario draft editor operations
 */
@RestController
@RequestMapping("/api/editor/drafts")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
@Tag(name = "Scenario Editor", description = "Scenario draft management endpoints")
public class ScenarioDraftController {

    private final ScenarioEditorService editorService;

    @GetMapping
    @Operation(summary = "Get all active drafts", description = "Retrieve all active scenario drafts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Drafts retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<DraftResponse>> getAllActive() {
        List<DraftResponse> drafts = editorService.findAllActive();
        return ResponseEntity.ok(drafts);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get drafts by status", description = "Retrieve drafts filtered by status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Drafts retrieved successfully")
    })
    public ResponseEntity<List<DraftResponse>> getByStatus(@PathVariable DraftStatus status) {
        List<DraftResponse> drafts = editorService.findByStatus(status);
        return ResponseEntity.ok(drafts);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get drafts by category", description = "Retrieve drafts filtered by category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Drafts retrieved successfully")
    })
    public ResponseEntity<List<DraftResponse>> getByCategory(@PathVariable String category) {
        List<DraftResponse> drafts = editorService.findByCategory(category);
        return ResponseEntity.ok(drafts);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get draft by ID", description = "Retrieve draft details including scenario JSON")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Draft found"),
            @ApiResponse(responseCode = "404", description = "Draft not found")
    })
    public ResponseEntity<DraftDetailResponse> getById(@PathVariable UUID id) {
        DraftDetailResponse draft = editorService.findById(id);
        return ResponseEntity.ok(draft);
    }

    @PostMapping
    @Operation(summary = "Create new draft", description = "Create a new scenario draft")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Draft created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Draft with slug already exists")
    })
    public ResponseEntity<DraftDetailResponse> create(
            @Valid @RequestBody CreateDraftRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getUserId(userDetails);
        DraftDetailResponse created = editorService.create(request, userId);
        URI location = URI.create("/api/editor/drafts/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update draft", description = "Update existing draft")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Draft updated successfully"),
            @ApiResponse(responseCode = "404", description = "Draft not found"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<DraftDetailResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDraftRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getUserId(userDetails);
        DraftDetailResponse updated = editorService.update(id, request, userId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete draft (admin only)", description = "Soft delete a draft")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Draft deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Draft not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        editorService.delete(id);
    }

    @PostMapping("/{id}/validate")
    @Operation(summary = "Validate draft", description = "Validate draft scenario structure")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Validation completed")
    })
    public ResponseEntity<ValidationResultResponse> validate(@PathVariable UUID id) {
        ValidationResultResponse result = editorService.validateDraft(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Publish draft to course (admin only)", description = "Publish validated draft as a course")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Draft published successfully"),
            @ApiResponse(responseCode = "400", description = "Draft validation failed or missing slug"),
            @ApiResponse(responseCode = "409", description = "Course with slug already exists")
    })
    public ResponseEntity<Map<String, UUID>> publish(
            @PathVariable UUID id,
            @Valid @RequestBody PublishDraftRequest request) {
        UUID courseId = editorService.publishDraft(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("courseId", courseId));
    }

    @GetMapping("/{id}/versions")
    @Operation(summary = "Get draft versions history", description = "Retrieve all versions of a draft")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Versions retrieved successfully")
    })
    public ResponseEntity<List<DraftVersionResponse>> getVersions(@PathVariable UUID id) {
        List<DraftVersionResponse> versions = editorService.getDraftVersions(id);
        return ResponseEntity.ok(versions);
    }

    @GetMapping("/{id}/versions/{versionNumber}")
    @Operation(summary = "Get specific draft version", description = "Retrieve specific version of a draft")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Version found"),
            @ApiResponse(responseCode = "404", description = "Version not found")
    })
    public ResponseEntity<DraftVersionResponse> getVersion(
            @PathVariable UUID id,
            @PathVariable Integer versionNumber) {
        DraftVersionResponse version = editorService.getDraftVersion(id, versionNumber);
        return ResponseEntity.ok(version);
    }

    @PostMapping("/{id}/versions/{versionNumber}/restore")
    @Operation(summary = "Restore draft to version", description = "Restore draft to a specific version")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Draft restored successfully"),
            @ApiResponse(responseCode = "404", description = "Draft or version not found")
    })
    public ResponseEntity<DraftDetailResponse> restoreVersion(
            @PathVariable UUID id,
            @PathVariable Integer versionNumber,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = getUserId(userDetails);
        DraftDetailResponse restored = editorService.restoreVersion(id, versionNumber, userId);
        return ResponseEntity.ok(restored);
    }

    private UUID getUserId(UserDetails userDetails) {
        return UUID.fromString(userDetails.getUsername());
    }
}
