package com.taskmanager.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentResponse {

    private Long id;
    private String fileName;
    private String filePath;
    private Long uploadedBy;
    private Long taskId;
    private LocalDateTime uploadedAt;
}