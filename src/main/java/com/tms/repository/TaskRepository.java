package com.tms.repository;

import com.tms.model.Task;
import com.tms.model.User;
import com.tms.model.enums.TaskPriority;
import com.tms.model.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Query("""
        SELECT t FROM Task t WHERE t.project.id = :projectId
        AND (:status IS NULL OR t.status = :status)
        AND (:priority IS NULL OR t.priority = :priority)
        AND (:assigneeId IS NULL OR t.assignee.id = :assigneeId)
        AND (:dueBefore IS NULL OR t.dueDate <= :dueBefore)
        AND (:dueAfter IS NULL OR t.dueDate >= :dueAfter)
        AND (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%',:search,'%'))
             OR LOWER(t.description) LIKE LOWER(CONCAT('%',:search,'%')))
        """)
    Page<Task> findByProjectWithFilters(
            @Param("projectId") UUID projectId,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("assigneeId") UUID assigneeId,
            @Param("dueBefore") LocalDate dueBefore,
            @Param("dueAfter") LocalDate dueAfter,
            @Param("search") String search,
            Pageable pageable);

    Page<Task> findByAssignee(User assignee, Pageable pageable);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignee = :user OR t.createdBy = :user")
    long countByUser(@Param("user") User user);

    @Query("SELECT COUNT(t) FROM Task t WHERE (t.assignee = :user OR t.createdBy = :user) AND t.status = :status")
    long countByUserAndStatus(@Param("user") User user, @Param("status") TaskStatus status);

    @Query("SELECT COUNT(t) FROM Task t WHERE (t.assignee = :user OR t.createdBy = :user) AND t.dueDate < :today AND t.status != 'DONE'")
    long countOverdueByUser(@Param("user") User user, @Param("today") LocalDate today);

    @Query("SELECT t FROM Task t WHERE (t.assignee = :user OR t.createdBy = :user) AND t.dueDate BETWEEN :from AND :to AND t.status != 'DONE' ORDER BY t.dueDate ASC")
    List<Task> findUpcomingByUser(@Param("user") User user, @Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT t FROM Task t WHERE t.assignee.id = :userId AND t.dueDate = :tomorrow AND t.status != 'DONE'")
    List<Task> findTasksDueOn(@Param("userId") UUID userId, @Param("tomorrow") LocalDate tomorrow);

    @Query("SELECT t FROM Task t WHERE t.dueDate = :dueDate AND t.status != 'DONE'")
    List<Task> findAllDueOn(@Param("dueDate") LocalDate dueDate);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.status = :status")
    long countByProjectAndStatus(@Param("projectId") UUID projectId, @Param("status") TaskStatus status);
}
