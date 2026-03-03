package com.tms.dto.response;

import com.tms.model.TaskAttachment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AttachmentResponse {
    private UUID id;
    private String fileName;
    private String originalName;
    private Long fileSize;
    private String contentType;
    private LocalDateTime createdAt;

    public static AttachmentResponse from(TaskAttachment a) {
        return AttachmentResponse.builder()
                .id(a.getId())
                .fileName(a.getFileName())
                .originalName(a.getOriginalName())
                .fileSize(a.getFileSize())
                .contentType(a.getContentType())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
