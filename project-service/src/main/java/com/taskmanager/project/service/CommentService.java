package com.taskmanager.project.service;

import com.taskmanager.project.dto.CommentRequest;
import com.taskmanager.project.dto.CommentResponse;
import com.taskmanager.project.entity.Comment;
import com.taskmanager.project.entity.Task;
import com.taskmanager.project.exception.CommentNotFoundException;
import com.taskmanager.project.exception.TaskNotFoundException;
import com.taskmanager.project.exception.UnauthorizedException;
import com.taskmanager.project.repository.CommentRepository;
import com.taskmanager.project.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public CommentResponse addComment(Long taskId, CommentRequest request, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUserId(userId);
        comment.setTask(task);

        Comment savedComment = commentRepository.save(comment);

        return mapToResponse(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException(taskId);
        }

        List<Comment> comments = commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
        return comments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, String content, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        if (!comment.getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this comment");
        }

        comment.setContent(content);
        Comment updatedComment = commentRepository.save(comment);

        return mapToResponse(updatedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        if (!comment.getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    private CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUserId())
                .userName("User " + comment.getUserId()) // Placeholder
                .taskId(comment.getTask().getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}