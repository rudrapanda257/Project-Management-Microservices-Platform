package com.taskmanager.notification.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskEvent {
    private String eventType;
    private Long taskId;
    private String taskTitle;
    private Long assigneeId;
    private String assigneeName;
    private Long projectId;
    private String projectName;
    private LocalDateTime timestamp;
}