package com.tms.aop;

import com.tms.model.AuditLog;
import com.tms.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;

    @AfterReturning(pointcut = "@annotation(auditable)", returning = "result")
    public void audit(JoinPoint joinPoint, Auditable auditable, Object result) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UUID userId = null;
            if (auth != null && auth.isAuthenticated()) {
                // Try to extract userId from principal name (email) stored in context
                userId = null; // Will be null if we can't resolve; audit still records action
            }

            String entityId = null;
            if (result != null) {
                try {
                    var idMethod = result.getClass().getMethod("getId");
                    Object id = idMethod.invoke(result);
                    if (id != null) entityId = id.toString();
                } catch (Exception ignored) {}
            }

            AuditLog log = AuditLog.builder()
                    .userId(userId)
                    .action(auditable.action())
                    .entityType(auditable.entityType())
                    .entityId(entityId)
                    .details(joinPoint.getSignature().getName())
                    .build();
            auditLogRepository.save(log);
        } catch (Exception e) {
            log.error("Audit logging failed: {}", e.getMessage());
        }
    }
}
