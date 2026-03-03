package com.tms.controller;

import com.tms.dto.request.*;
import com.tms.dto.response.*;
import com.tms.model.enums.TaskPriority;
import com.tms.model.enums.TaskStatus;
import com.tms.service.CommentService;
import com.tms.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;
    private final CommentService commentService;

    @GetMapping
    @Operation(summary = "List tasks in a project")
    public ResponseEntity<ApiResponse<PagedResponse<TaskResponse>>> listTasks(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) UUID assigneeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueBefore,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueAfter,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        String[] sortParts = sort.split(",");
        Sort pageSort = sortParts.length == 2 && sortParts[1].equalsIgnoreCase("asc")
                ? Sort.by(sortParts[0]).ascending() : Sort.by(sortParts[0]).descending();
        return ResponseEntity.ok(ApiResponse.success(
                taskService.listTasks(projectId, user.getUsername(), status, priority,
                        assigneeId, dueBefore, dueAfter, search,
                        PageRequest.of(page, Math.min(size, 100), pageSort))));
    }

    @PostMapping
    @Operation(summary = "Create a task")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(taskService.createTask(projectId, user.getUsername(), request), "Task created"));
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTask(projectId, taskId, user.getUsername())));
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Update task")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                taskService.updateTask(projectId, taskId, user.getUsername(), request), "Task updated"));
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete task")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserDetails user) {
        taskService.deleteTask(projectId, taskId, user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null, "Task deleted"));
    }

    @PatchMapping("/{taskId}/status")
    @Operation(summary = "Update task status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateStatus(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                taskService.updateStatus(projectId, taskId, user.getUsername(), request), "Status updated"));
    }

    @PatchMapping("/{taskId}/assign")
    @Operation(summary = "Assign task")
    public ResponseEntity<ApiResponse<TaskResponse>> assignTask(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserDetails user,
            @RequestBody AssignTaskRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                taskService.assignTask(projectId, taskId, user.getUsername(), request), "Task assigned"));
    }

    @GetMapping("/my-tasks")
    @Operation(summary = "Get tasks assigned to current user across all projects")
    public ResponseEntity<ApiResponse<PagedResponse<TaskResponse>>> getMyTasks(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                taskService.getMyTasks(user.getUsername(),
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/{taskId}/comments")
    @Operation(summary = "List task comments")
    public ResponseEntity<ApiResponse<PagedResponse<CommentResponse>>> listComments(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                commentService.listComments(projectId, taskId, user.getUsername(),
                        PageRequest.of(page, size))));
    }

    @PostMapping("/{taskId}/comments")
    @Operation(summary = "Add comment to task")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        commentService.addComment(projectId, taskId, user.getUsername(), request), "Comment added"));
    }

    @DeleteMapping("/{taskId}/comments/{commentId}")
    @Operation(summary = "Delete comment")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @PathVariable UUID commentId,
            @AuthenticationPrincipal UserDetails user) {
        commentService.deleteComment(projectId, taskId, commentId, user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null, "Comment deleted"));
    }
}
