package com.tms.service;

import com.tms.dto.response.DashboardSummaryResponse;
import com.tms.dto.response.TaskResponse;
import com.tms.model.User;
import com.tms.model.enums.TaskStatus;
import com.tms.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    @Cacheable(value = "dashboardSummary", key = "#userEmail")
    public DashboardSummaryResponse getSummary(String userEmail) {
        User user = userService.getUserEntityByEmail(userEmail);
        long total = taskRepository.countByUser(user);
        long completed = taskRepository.countByUserAndStatus(user, TaskStatus.DONE);
        long inProgress = taskRepository.countByUserAndStatus(user, TaskStatus.IN_PROGRESS);
        long overdue = taskRepository.countOverdueByUser(user, LocalDate.now());
        return DashboardSummaryResponse.builder()
                .totalTasks(total)
                .completedTasks(completed)
                .inProgressTasks(inProgress)
                .overdueTasks(overdue)
                .build();
    }

    public List<TaskResponse> getUpcomingTasks(String userEmail) {
        User user = userService.getUserEntityByEmail(userEmail);
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);
        return taskRepository.findUpcomingByUser(user, today, nextWeek)
                .stream().map(TaskResponse::from).toList();
    }
}
