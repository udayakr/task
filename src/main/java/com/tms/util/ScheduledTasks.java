package com.tms.util;

import com.tms.model.Task;
import com.tms.repository.TaskRepository;
import com.tms.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final TaskRepository taskRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 8 * * *")
    public void sendDueDateReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Task> dueTomorrow = taskRepository.findAllDueOn(tomorrow);
        log.info("Sending due date reminders for {} tasks", dueTomorrow.size());
        for (Task task : dueTomorrow) {
            if (task.getAssignee() != null) {
                emailService.sendTaskDueReminderEmail(
                        task.getAssignee().getEmail(),
                        task.getAssignee().getFirstName(),
                        task.getTitle(),
                        tomorrow.toString()
                );
            }
        }
    }
}
