package com.taskmanager.project.dto;

import com.taskmanager.project.enums.Priority;
import com.taskmanager.project.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskSearchCriteria {

    private Long projectId;
    private Long assigneeId;
    private Status status;
    private Priority priority;
    private LocalDate dueDateFrom;
    private LocalDate dueDateTo;
}