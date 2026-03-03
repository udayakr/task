package com.tms.service;

import com.tms.dto.request.CreateProjectRequest;
import com.tms.dto.request.UpdateProjectRequest;
import com.tms.dto.response.PagedResponse;
import com.tms.dto.response.ProjectResponse;
import com.tms.dto.response.ProjectStatsResponse;
import com.tms.dto.response.UserResponse;
import com.tms.exception.ForbiddenException;
import com.tms.exception.ResourceNotFoundException;
import com.tms.model.Project;
import com.tms.model.User;
import com.tms.model.enums.ProjectStatus;
import com.tms.model.enums.TaskStatus;
import com.tms.repository.ProjectRepository;
import com.tms.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;

    public PagedResponse<ProjectResponse> listProjects(String userEmail, Pageable pageable) {
        User user = userService.getUserEntityByEmail(userEmail);
        return PagedResponse.from(
                projectRepository.findByOwnerOrMember(user, pageable).map(ProjectResponse::from));
    }

    public ProjectResponse createProject(String userEmail, CreateProjectRequest request) {
        User owner = userService.getUserEntityByEmail(userEmail);
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(owner)
                .build();
        project.getMembers().add(owner);
        return ProjectResponse.from(projectRepository.save(project));
    }

    public ProjectResponse getProject(UUID projectId, String userEmail) {
        Project project = findAndAuthorize(projectId, userEmail);
        return ProjectResponse.from(project);
    }

    public ProjectResponse updateProject(UUID projectId, String userEmail, UpdateProjectRequest request) {
        Project project = findAndAuthorize(projectId, userEmail);
        requireOwner(project, userEmail);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        return ProjectResponse.from(projectRepository.save(project));
    }

    public void archiveProject(UUID projectId, String userEmail) {
        Project project = findAndAuthorize(projectId, userEmail);
        requireOwner(project, userEmail);
        project.setStatus(ProjectStatus.ARCHIVED);
        projectRepository.save(project);
    }

    public List<UserResponse> getMembers(UUID projectId, String userEmail) {
        Project project = findAndAuthorize(projectId, userEmail);
        return project.getMembers().stream().map(UserResponse::from).toList();
    }

    public void addMember(UUID projectId, UUID userId, String userEmail) {
        Project project = findAndAuthorize(projectId, userEmail);
        requireOwner(project, userEmail);
        User newMember = userService.getUserEntityByEmail(
                userService.getUserById(userId).getEmail());
        project.getMembers().add(newMember);
        projectRepository.save(project);
    }

    public void removeMember(UUID projectId, UUID userId, String userEmail) {
        Project project = findAndAuthorize(projectId, userEmail);
        requireOwner(project, userEmail);
        project.getMembers().removeIf(m -> m.getId().equals(userId));
        projectRepository.save(project);
    }

    @Cacheable(value = "projectStats", key = "#projectId")
    public ProjectStatsResponse getStats(UUID projectId, String userEmail) {
        findAndAuthorize(projectId, userEmail);
        long total = taskRepository.countByProjectAndStatus(projectId, null) +
                taskRepository.countByProjectAndStatus(projectId, TaskStatus.TODO) +
                taskRepository.countByProjectAndStatus(projectId, TaskStatus.IN_PROGRESS) +
                taskRepository.countByProjectAndStatus(projectId, TaskStatus.REVIEW) +
                taskRepository.countByProjectAndStatus(projectId, TaskStatus.DONE);
        // Simplified counts
        long todo = taskRepository.countByProjectAndStatus(projectId, TaskStatus.TODO);
        long inProgress = taskRepository.countByProjectAndStatus(projectId, TaskStatus.IN_PROGRESS);
        long review = taskRepository.countByProjectAndStatus(projectId, TaskStatus.REVIEW);
        long done = taskRepository.countByProjectAndStatus(projectId, TaskStatus.DONE);
        long totalReal = todo + inProgress + review + done;
        return ProjectStatsResponse.builder()
                .total(totalReal).todo(todo).inProgress(inProgress).review(review).done(done)
                .build();
    }

    public Project findAndAuthorize(UUID projectId, String userEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));
        User user = userService.getUserEntityByEmail(userEmail);
        boolean isOwner = project.getOwner().getId().equals(user.getId());
        boolean isMember = project.getMembers().stream().anyMatch(m -> m.getId().equals(user.getId()));
        if (!isOwner && !isMember) {
            throw new ForbiddenException("You don't have access to this project");
        }
        return project;
    }

    private void requireOwner(Project project, String userEmail) {
        User user = userService.getUserEntityByEmail(userEmail);
        if (!project.getOwner().getId().equals(user.getId())) {
            throw new ForbiddenException("Only the project owner can perform this action");
        }
    }
}
