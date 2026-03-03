package com.tms.controller;

import com.tms.dto.response.ApiResponse;
import com.tms.dto.response.AttachmentResponse;
import com.tms.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/tasks/{taskId}/attachments")
@RequiredArgsConstructor
@Tag(name = "Attachments")
@SecurityRequirement(name = "bearerAuth")
public class AttachmentController {

    private final FileStorageService fileStorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload task attachment")
    public ResponseEntity<ApiResponse<AttachmentResponse>> upload(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserDetails user,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        fileStorageService.uploadAttachment(projectId, taskId, user.getUsername(), file),
                        "File uploaded"));
    }

    @GetMapping
    @Operation(summary = "List task attachments")
    public ResponseEntity<ApiResponse<List<AttachmentResponse>>> list(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.success(
                fileStorageService.listAttachments(projectId, taskId, user.getUsername())));
    }
}
