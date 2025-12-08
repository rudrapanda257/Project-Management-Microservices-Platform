package com.taskmanager.project.repository;

import com.taskmanager.project.entity.Task;
import com.taskmanager.project.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

        @Query("SELECT t FROM Task t WHERE t.project.id = :projectId")
        Page<Task> findByProjectId(@Param("projectId") Long projectId, Pageable pageable);

        @Query("SELECT t FROM Task t WHERE t.assigneeId = :assigneeId AND t.status = :status")
        List<Task> findByAssigneeIdAndStatus(@Param("assigneeId") Long assigneeId,
                        @Param("status") String status);

        @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.status = :status")
        List<Task> findByProjectIdAndStatus(@Param("projectId") Long projectId,
                        @Param("status") Status status);

        @Query("SELECT t FROM Task t WHERE t.dueDate < CURRENT_DATE AND t.status != com.taskmanager.project.enums.Status.DONE")
        List<Task> findOverdueTasks();

        @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.status = :status")
        Long countByProjectIdAndStatus(@Param("projectId") Long projectId,
                        @Param("status") Status status);

        @Query("SELECT t FROM Task t WHERE t.assigneeId = :assigneeId")
        List<Task> findByAssigneeId(@Param("assigneeId") Long assigneeId);

        Long countByProjectIdAndStatus(Long projectId, String status);
}