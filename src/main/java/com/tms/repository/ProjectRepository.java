package com.tms.repository;

import com.tms.model.Project;
import com.tms.model.User;
import com.tms.model.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Query("SELECT p FROM Project p WHERE p.owner = :user OR :user MEMBER OF p.members")
    Page<Project> findByOwnerOrMember(@Param("user") User user, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE (p.owner = :user OR :user MEMBER OF p.members) AND p.status = :status")
    Page<Project> findByOwnerOrMemberAndStatus(@Param("user") User user,
                                               @Param("status") ProjectStatus status,
                                               Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Project p WHERE p.id = :id AND (p.owner = :user OR :user MEMBER OF p.members)")
    boolean isMemberOrOwner(@Param("id") UUID id, @Param("user") User user);
}
