package com.tms.service;

import com.tms.dto.request.CreateCommentRequest;
import com.tms.dto.response.CommentResponse;
import com.tms.dto.response.PagedResponse;
import com.tms.exception.ForbiddenException;
import com.tms.exception.ResourceNotFoundException;
import com.tms.model.Comment;
import com.tms.model.Task;
import com.tms.model.User;
import com.tms.repository.CommentRepository;
import com.tms.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    private final UserService userService;

    public PagedResponse<CommentResponse> listComments(UUID projectId, UUID taskId,
                                                        String userEmail, Pageable pageable) {
        projectService.findAndAuthorize(projectId, userEmail);
        return PagedResponse.from(
                commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId, pageable)
                        .map(CommentResponse::from));
    }

    public CommentResponse addComment(UUID projectId, UUID taskId,
                                      String userEmail, CreateCommentRequest request) {
        projectService.findAndAuthorize(projectId, userEmail);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));
        User author = userService.getUserEntityByEmail(userEmail);
        Comment comment = Comment.builder()
                .content(request.getContent())
                .task(task)
                .author(author)
                .build();
        return CommentResponse.from(commentRepository.save(comment));
    }

    public void deleteComment(UUID projectId, UUID taskId, UUID commentId, String userEmail) {
        projectService.findAndAuthorize(projectId, userEmail);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));
        User requestingUser = userService.getUserEntityByEmail(userEmail);
        boolean isAuthor = comment.getAuthor().getId().equals(requestingUser.getId());
        boolean isAdmin = requestingUser.getRole().name().equals("ADMIN");
        if (!isAuthor && !isAdmin) {
            throw new ForbiddenException("Only the comment author or admin can delete this comment");
        }
        commentRepository.delete(comment);
    }
}
