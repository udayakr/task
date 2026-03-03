package com.tms.service;

import com.tms.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    @Async
    public void sendVerificationEmail(String to, String firstName, String token) {
        Context ctx = new Context();
        ctx.setVariable("firstName", firstName);
        ctx.setVariable("verificationUrl",
                appProperties.getFrontendUrl() + "/verify-email?token=" + token);
        sendEmail(to, "Verify your TMS account", "email/verification", ctx);
    }

    @Async
    public void sendPasswordResetEmail(String to, String firstName, String token) {
        Context ctx = new Context();
        ctx.setVariable("firstName", firstName);
        ctx.setVariable("resetUrl",
                appProperties.getFrontendUrl() + "/reset-password?token=" + token);
        sendEmail(to, "Reset your TMS password", "email/password-reset", ctx);
    }

    @Async
    public void sendTaskAssignedEmail(String to, String firstName, String taskTitle, String projectName) {
        Context ctx = new Context();
        ctx.setVariable("firstName", firstName);
        ctx.setVariable("taskTitle", taskTitle);
        ctx.setVariable("projectName", projectName);
        ctx.setVariable("dashboardUrl", appProperties.getFrontendUrl() + "/tasks/my-tasks");
        sendEmail(to, "Task assigned to you: " + taskTitle, "email/task-assigned", ctx);
    }

    @Async
    public void sendTaskDueReminderEmail(String to, String firstName, String taskTitle, String dueDate) {
        Context ctx = new Context();
        ctx.setVariable("firstName", firstName);
        ctx.setVariable("taskTitle", taskTitle);
        ctx.setVariable("dueDate", dueDate);
        ctx.setVariable("dashboardUrl", appProperties.getFrontendUrl() + "/tasks/my-tasks");
        sendEmail(to, "Task due tomorrow: " + taskTitle, "email/task-reminder", ctx);
    }

    private void sendEmail(String to, String subject, String template, Context ctx) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("noreply@tms.com");
            String html = templateEngine.process(template, ctx);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
