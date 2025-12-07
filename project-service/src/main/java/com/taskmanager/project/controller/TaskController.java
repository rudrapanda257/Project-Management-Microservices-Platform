package com.taskmanager.project.controller;

import com.taskmanager.project.dto.TaskRequest;
import com.taskmanager.project.dto.TaskResponse;
import com.taskmanager.project.dto.TaskSearchCriteria;
import com.taskmanager.project.enums.Priority;
import com.taskmanager.project.enums.Status;
import com.taskmanager.project.service.TaskSearchService;
import com.taskmanager.project.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;
    private final TaskSearchService taskSearchService;

    /**
     * Create a task in a project
     */
    @PostMapping("/api/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody TaskRequest request) {
        Long userId = getCurrentUserId();
        log.info("Request to create task in project: {} by user: {}", projectId, userId);

        TaskResponse response = taskService.createTask(projectId, request, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all tasks for a project with pagination
     */
    @GetMapping("/api/projects/{projectId}/tasks")
    public ResponseEntity<Page<TaskResponse>> getTasksByProject(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Request to get tasks for project: {}", projectId);

        Page<TaskResponse> tasks = taskService.getTasksByProject(projectId, page, size);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get task by ID
     */
    @GetMapping("/api/projects/{projectId}/tasks/task/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long taskId) {
        log.info("Request to get task by ID: {}", taskId);

        TaskResponse task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    /**
     * Update task
     */
    @PutMapping("/api/projects/{projectId}/tasks/task/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequest request) {
        Long userId = getCurrentUserId();
        log.info("Request to update task ID: {} by user: {}", taskId, userId);

        TaskResponse response = taskService.updateTask(taskId, request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update task status only
     */
    @PatchMapping("/api/projects/{projectId}/tasks/task/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestBody Map<String, String> statusUpdate) {
        Long userId = getCurrentUserId();
        String status = statusUpdate.get("status");
        log.info("Request to update task status ID: {} to: {}", taskId, status);

        TaskResponse response = taskService.updateTaskStatus(taskId, status, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete task
     */
    @DeleteMapping("/api/projects/{projectId}/tasks/task/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        Long userId = getCurrentUserId();
        log.info("Request to delete task ID: {} by user: {}", taskId, userId);

        taskService.deleteTask(taskId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get tasks assigned to current user
     */
    @GetMapping("/api/tasks/my")
    public ResponseEntity<List<TaskResponse>> getMyTasks(
            @RequestParam(required = false) String status) {
        Long userId = getCurrentUserId();
        log.info("Request to get my tasks for user: {} with status: {}", userId, status);

        List<TaskResponse> tasks = taskService.getMyTasks(userId, status);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get all overdue tasks
     */
    @GetMapping("/api/tasks/overdue")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks() {
        log.info("Request to get overdue tasks");

        List<TaskResponse> tasks = taskService.getOverdueTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Search tasks with dynamic criteria
     * ✅ FIXED: Now returns Page<TaskResponse> directly from service
     */
    @GetMapping("/api/tasks/search")
    public ResponseEntity<Page<TaskResponse>> searchTasks(
            @RequestParam(value = "projectId", required = false) Long projectId,
            @RequestParam(value = "assigneeId", required = false) Long assigneeId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "priority", required = false) String priority,
            @RequestParam(value = "dueDateFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateFrom,
            @RequestParam(value = "dueDateTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateTo,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        log.info("Request to search tasks with criteria - projectId: {}, status: {}, priority: {}",
                projectId, status, priority);

        TaskSearchCriteria criteria = TaskSearchCriteria.builder()
                .projectId(projectId)
                .assigneeId(assigneeId)
                .status(status != null ? Status.valueOf(status) : null)
                .priority(priority != null ? Priority.valueOf(priority) : null)
                .dueDateFrom(dueDateFrom)
                .dueDateTo(dueDateTo)
                .build();

        Pageable pageable = PageRequest.of(page, size);

        // ✅ FIXED: Service now returns Page<TaskResponse>, no need to map here
        Page<TaskResponse> results = taskSearchService.searchTasks(criteria, pageable);

        return ResponseEntity.ok(results);
    }

    /**
     * Extract current user ID from JWT token
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdStr = authentication.getName();
        return Long.parseLong(userIdStr);
    }
}