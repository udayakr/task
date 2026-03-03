package com.tms.repository;

import com.tms.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    @Query(value = """
        SELECT a FROM AuditLog a
        WHERE a.userId IN (
            SELECT t.assignee.id FROM Task t WHERE t.project.id IN
                (SELECT p.id FROM Project p WHERE p.owner.id = :userId OR :userId IN
                    (SELECT u.id FROM p.members u))
            UNION
            SELECT t.createdBy.id FROM Task t WHERE t.project.id IN
                (SELECT p2.id FROM Project p2 WHERE p2.owner.id = :userId)
        )
        OR a.userId = :userId
        ORDER BY a.createdAt DESC
        """)
    List<AuditLog> findRecentActivityForUser(@Param("userId") UUID userId, Pageable pageable);

    Page<AuditLog> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
