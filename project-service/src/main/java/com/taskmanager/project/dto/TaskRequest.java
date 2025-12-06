package com.taskmanager.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    
    @NotBlank(message = "Task title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Assignee ID is required")
    private Long assigneeId;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    @NotBlank(message = "Priority is required")
    private String priority;
    
    private LocalDate dueDate;
}