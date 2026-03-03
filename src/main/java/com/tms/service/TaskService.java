package com.tms.service;

import com.tms.dto.request.*;
import com.tms.dto.response.PagedResponse;
import com.tms.dto.response.TaskResponse;
import com.tms.exception.ForbiddenException;
import com.tms.exception.ResourceNotFoundException;
import com.tms.model.*;
import com.tms.model.enums.TaskPriority;
import com.tms.model.enums.TaskStatus;
import com.tms.repository.TaskRepository;
import com.tms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final UserService userService;

    public PagedResponse<TaskResponse> listTasks(UUID projectId, String userEmail,
                                                  TaskStatus status, TaskPriority priority,
                                                  UUID assigneeId, LocalDate dueBefore,
                                                  LocalDate dueAfter, String search,
                                                  Pageable pageable) {
        projectService.findAndAuthorize(projectId, userEmail);
        return PagedResponse.from(
                taskRepository.findByProjectWithFilters(projectId, status, priority,
                        assigneeId, dueBefore, dueAfter, search, pageable)
                        .map(TaskResponse::from));
    }

    public TaskResponse createTask(UUID projectId, String userEmail, CreateTaskRequest request) {
        Project project = projectService.findAndAuthorize(projectId, userEmail);
        User creator = userService.getUserEntityByEmail(userEmail);
        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.getAssigneeId()));
        }
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM)
                .dueDate(request.getDueDate())
                .project(project)
                .createdBy(creator)
                .assignee(assignee)
                .tags(request.getTags())
                .estimatedHours(request.getEstimatedHours())
                .build();
        return TaskResponse.from(taskRepository.save(task));
    }

    @Cacheable(value = "taskCache", key = "#taskId")
    public TaskResponse getTask(UUID projectId, UUID taskId, String userEmail) {
        projectService.findAndAuthorize(projectId, userEmail);
        return TaskResponse.from(findTask(taskId, projectId));
    }

    @CacheEvict(value = "taskCache", key = "#taskId")
    public TaskResponse updateTask(UUID projectId, UUID taskId, String userEmail, UpdateTaskRequest request) {
        projectService.findAndAuthorize(projectId, userEmail);
        Task task = findTask(taskId, projectId);
        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.getAssigneeId()));
        }
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setAssignee(assignee);
        task.setTags(request.getTags());
        task.setEstimatedHours(request.getEstimatedHours());
        task.setActualHours(request.getActualHours());
        return TaskResponse.from(taskRepository.save(task));
    }

    @CacheEvict(value = "taskCache", key = "#taskId")
    public void deleteTask(UUID projectId, UUID taskId, String userEmail) {
        projectService.findAndAuthorize(projectId, userEmail);
        Task task = findTask(taskId, projectId);
        User requestingUser = userService.getUserEntityByEmail(userEmail);
        boolean isCreator = task.getCreatedBy().getId().equals(requestingUser.getId());
        boolean isAssignee = task.getAssignee() != null && task.getAssignee().getId().equals(requestingUser.getId());
        boolean isOwner = task.getProject().getOwner().getId().equals(requestingUser.getId());
        if (!isCreator && !isAssignee && !isOwner) {
            throw new ForbiddenException("You don't have permission to delete this task");
        }
        taskRepository.delete(task);
    }

    @CacheEvict(value = "taskCache", key = "#taskId")
    public TaskResponse updateStatus(UUID projectId, UUID taskId, String userEmail, UpdateStatusRequest request) {
        projectService.findAndAuthorize(projectId, userEmail);
        Task task = findTask(taskId, projectId);
        task.setStatus(request.getStatus());
        return TaskResponse.from(taskRepository.save(task));
    }

    @CacheEvict(value = "taskCache", key = "#taskId")
    public TaskResponse assignTask(UUID projectId, UUID taskId, String userEmail, AssignTaskRequest request) {
        projectService.findAndAuthorize(projectId, userEmail);
        Task task = findTask(taskId, projectId);
        if (request.getAssigneeId() == null) {
            task.setAssignee(null);
        } else {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.getAssigneeId()));
            task.setAssignee(assignee);
        }
        return TaskResponse.from(taskRepository.save(task));
    }

    public PagedResponse<TaskResponse> getMyTasks(String userEmail, Pageable pageable) {
        User user = userService.getUserEntityByEmail(userEmail);
        return PagedResponse.from(taskRepository.findByAssignee(user, pageable).map(TaskResponse::from));
    }

    private Task findTask(UUID taskId, UUID projectId) {
        return taskRepository.findById(taskId)
                .filter(t -> t.getProject().getId().equals(projectId))
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));
    }
}
