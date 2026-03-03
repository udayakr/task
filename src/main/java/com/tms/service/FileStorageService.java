package com.tms.service;

import com.tms.config.AppProperties;
import com.tms.dto.response.AttachmentResponse;
import com.tms.exception.BadRequestException;
import com.tms.exception.ResourceNotFoundException;
import com.tms.model.Task;
import com.tms.model.TaskAttachment;
import com.tms.model.User;
import com.tms.repository.TaskAttachmentRepository;
import com.tms.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FileStorageService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024L; // 10 MB
    private static final int MAX_FILES_PER_TASK = 5;
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf", "image/png", "image/jpeg",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain"
    );

    private final TaskAttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final AppProperties appProperties;

    public AttachmentResponse uploadAttachment(UUID projectId, UUID taskId,
                                               String userEmail, MultipartFile file) throws IOException {
        projectService.findAndAuthorize(projectId, userEmail);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds limit of 10MB");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("File type not allowed. Accepted: PDF, PNG, JPG, DOCX, XLSX, TXT");
        }
        if (attachmentRepository.countByTaskId(taskId) >= MAX_FILES_PER_TASK) {
            throw new BadRequestException("Maximum of 5 attachments per task allowed");
        }

        String storedName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(appProperties.getStorage().getLocalPath());
        Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(storedName);
        Files.copy(file.getInputStream(), filePath);

        User uploader = userService.getUserEntityByEmail(userEmail);
        TaskAttachment attachment = TaskAttachment.builder()
                .fileName(storedName)
                .originalName(file.getOriginalFilename())
                .filePath(filePath.toString())
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .task(task)
                .uploadedBy(uploader)
                .build();
        return AttachmentResponse.from(attachmentRepository.save(attachment));
    }

    public List<AttachmentResponse> listAttachments(UUID projectId, UUID taskId, String userEmail) {
        projectService.findAndAuthorize(projectId, userEmail);
        return attachmentRepository.findByTaskId(taskId)
                .stream().map(AttachmentResponse::from).toList();
    }
}
