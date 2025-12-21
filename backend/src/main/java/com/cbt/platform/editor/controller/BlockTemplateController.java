package com.cbt.platform.editor.controller;

import com.cbt.platform.editor.dto.BlockTemplateResponse;
import com.cbt.platform.editor.dto.CreateBlockTemplateRequest;
import com.cbt.platform.editor.dto.UpdateBlockTemplateRequest;
import com.cbt.platform.editor.service.BlockTemplateService;
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
 * REST controller for block template operations
 */
@RestController
@RequestMapping("/api/editor/templates/blocks")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
@Tag(name = "Block Templates", description = "Block template management endpoints")
public class BlockTemplateController {

    private final BlockTemplateService templateService;

    @GetMapping
    @Operation(summary = "Get all active block templates", description = "Retrieve all active block templates")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Templates retrieved successfully")
    })
    public ResponseEntity<List<BlockTemplateResponse>> getAllActive() {
        List<BlockTemplateResponse> templates = templateService.findAllActive();
        return ResponseEntity.ok(templates);
    }

    @GetMapping("/type/{blockType}")
    @Operation(summary = "Get templates by block type", description = "Retrieve templates filtered by block type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Templates retrieved successfully")
    })
    public ResponseEntity<List<BlockTemplateResponse>> getByBlockType(@PathVariable String blockType) {
        List<BlockTemplateResponse> templates = templateService.findByBlockType(blockType);
        return ResponseEntity.ok(templates);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get templates by category", description = "Retrieve templates filtered by category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Templates retrieved successfully")
    })
    public ResponseEntity<List<BlockTemplateResponse>> getByCategory(@PathVariable String category) {
        List<BlockTemplateResponse> templates = templateService.findByCategory(category);
        return ResponseEntity.ok(templates);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get template by ID", description = "Retrieve template details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Template found"),
            @ApiResponse(responseCode = "404", description = "Template not found")
    })
    public ResponseEntity<BlockTemplateResponse> getById(@PathVariable UUID id) {
        BlockTemplateResponse template = templateService.findById(id);
        return ResponseEntity.ok(template);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new block template (admin only)", description = "Create a new block template")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Template created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<BlockTemplateResponse> create(@Valid @RequestBody CreateBlockTemplateRequest request) {
        BlockTemplateResponse created = templateService.create(request);
        URI location = URI.create("/api/editor/templates/blocks/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update block template (admin only)", description = "Update existing template")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Template updated successfully"),
            @ApiResponse(responseCode = "404", description = "Template not found"),
            @ApiResponse(responseCode = "403", description = "Cannot modify system template")
    })
    public ResponseEntity<BlockTemplateResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBlockTemplateRequest request) {
        BlockTemplateResponse updated = templateService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete block template (admin only)", description = "Soft delete a template")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Template deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Template not found"),
            @ApiResponse(responseCode = "403", description = "Cannot delete system template")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        templateService.delete(id);
    }
}
