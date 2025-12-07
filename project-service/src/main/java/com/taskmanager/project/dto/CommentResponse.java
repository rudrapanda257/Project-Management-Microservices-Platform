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
public class CommentResponse {

    private Long id;
    private String content;
    private Long userId;
    private String userName; // Placeholder - will be populated from user service later
    private Long taskId;
    private LocalDateTime createdAt;
}