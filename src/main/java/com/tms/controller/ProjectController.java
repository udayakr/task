package com.tms.controller;

import com.tms.dto.request.CreateProjectRequest;
import com.tms.dto.request.UpdateProjectRequest;
import com.tms.dto.response.*;
import com.tms.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projects")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    @Operation(summary = "List projects for current user")
    public ResponseEntity<ApiResponse<PagedResponse<ProjectResponse>>> listProjects(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                projectService.listProjects(user.getUsername(),
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @PostMapping
    @Operation(summary = "Create a project")
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody CreateProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(projectService.createProject(user.getUsername(), request), "Project created"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(projectService.getProject(id, user.getUsername())));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update project")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody UpdateProjectRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                projectService.updateProject(id, user.getUsername(), request), "Project updated"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Archive project")
    public ResponseEntity<ApiResponse<Void>> archiveProject(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails user) {
        projectService.archiveProject(id, user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null, "Project archived"));
    }

    @GetMapping("/{id}/members")
    @Operation(summary = "List project members")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getMembers(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(projectService.getMembers(id, user.getUsername())));
    }

    @PostMapping("/{id}/members/{userId}")
    @Operation(summary = "Add member to project")
    public ResponseEntity<ApiResponse<Void>> addMember(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            @AuthenticationPrincipal UserDetails user) {
        projectService.addMember(id, userId, user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null, "Member added"));
    }

    @DeleteMapping("/{id}/members/{userId}")
    @Operation(summary = "Remove member from project")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            @AuthenticationPrincipal UserDetails user) {
        projectService.removeMember(id, userId, user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null, "Member removed"));
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "Get project statistics")
    public ResponseEntity<ApiResponse<ProjectStatsResponse>> getStats(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(projectService.getStats(id, user.getUsername())));
    }
}
