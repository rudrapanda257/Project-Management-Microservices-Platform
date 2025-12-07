package com.taskmanager.project.controller;

import com.taskmanager.project.dto.AttachmentRequest;
import com.taskmanager.project.dto.AttachmentResponse;
import com.taskmanager.project.service.AttachmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping
    public ResponseEntity<AttachmentResponse> uploadAttachment(
            @PathVariable Long taskId,
            @Valid @RequestBody AttachmentRequest request) {
        Long userId = getCurrentUserId();
        AttachmentResponse response = attachmentService.uploadAttachment(
                taskId, request.getFileName(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AttachmentResponse>> getAttachmentsByTask(@PathVariable Long taskId) {
        List<AttachmentResponse> attachments = attachmentService.getAttachmentsByTask(taskId);
        return ResponseEntity.ok(attachments);
    }

    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable Long taskId,
            @PathVariable Long attachmentId) {
        Long userId = getCurrentUserId();
        attachmentService.deleteAttachment(attachmentId, userId);
        return ResponseEntity.noContent().build();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }
}