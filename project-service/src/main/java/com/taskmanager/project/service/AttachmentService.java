package com.taskmanager.project.service;

import com.taskmanager.project.dto.AttachmentResponse;
import com.taskmanager.project.entity.Attachment;
import com.taskmanager.project.entity.Task;
import com.taskmanager.project.exception.AttachmentNotFoundException;
import com.taskmanager.project.exception.TaskNotFoundException;
import com.taskmanager.project.exception.UnauthorizedException;
import com.taskmanager.project.repository.AttachmentRepository;
import com.taskmanager.project.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public AttachmentResponse uploadAttachment(Long taskId, String fileName, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        // Mock file path - real implementation would handle actual file upload
        String mockFilePath = "/uploads/" + fileName;

        Attachment attachment = new Attachment();
        attachment.setFileName(fileName);
        attachment.setFilePath(mockFilePath);
        attachment.setUploadedBy(userId);
        attachment.setTask(task);

        Attachment savedAttachment = attachmentRepository.save(attachment);

        return mapToResponse(savedAttachment);
    }

    @Transactional(readOnly = true)
    public List<AttachmentResponse> getAttachmentsByTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException(taskId);
        }

        List<Attachment> attachments = attachmentRepository.findByTaskId(taskId);
        return attachments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAttachment(Long attachmentId, Long userId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException(attachmentId));

        if (!attachment.getUploadedBy().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this attachment");
        }

        attachmentRepository.delete(attachment);
    }

    private AttachmentResponse mapToResponse(Attachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .fileName(attachment.getFileName())
                .filePath(attachment.getFilePath())
                .uploadedBy(attachment.getUploadedBy())
                .taskId(attachment.getTask().getId())
                .uploadedAt(attachment.getUploadedAt())
                .build();
    }
}