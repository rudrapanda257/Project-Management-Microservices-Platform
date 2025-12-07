package com.taskmanager.project.repository;

import com.taskmanager.project.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTaskIdOrderByCreatedAtDesc(Long taskId);

    List<Comment> findByUserId(Long userId, Pageable pageable);

    Long countByTaskId(Long taskId);
}