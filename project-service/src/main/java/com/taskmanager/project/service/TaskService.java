package com.taskmanager.project.service;

import com.taskmanager.project.client.UserServiceClient;
import com.taskmanager.project.client.dto.UserDTO;
import com.taskmanager.project.dto.TaskRequest;
import com.taskmanager.project.dto.TaskResponse;
import com.taskmanager.project.entity.Project;
import com.taskmanager.project.entity.Task;
import com.taskmanager.project.enums.Status;
import com.taskmanager.project.enums.Priority;
import com.taskmanager.project.kafka.TaskEventProducer;
import com.taskmanager.project.kafka.dto.TaskEvent;
import com.taskmanager.project.repository.ProjectRepository;
import com.taskmanager.project.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserServiceClient userServiceClient;
    private final TaskEventProducer taskEventProducer;

    @Transactional
    public TaskResponse createTask(Long projectId, TaskRequest request, Long userId) {
        // Validate project exists
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        // Fetch user details using Feign Client
        UserDTO assignee = userServiceClient.getUserById(request.getAssigneeId());
        log.info("Fetched assignee details: {}", assignee.getName());

        // Create task
        Task task = new Task();
        task.setProject(project);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setAssigneeId(request.getAssigneeId());
        task.setStatus(Status.valueOf(request.getStatus()));
        task.setPriority(Priority.valueOf(request.getPriority()));
        task.setDueDate(request.getDueDate());

        Task savedTask = taskRepository.save(task);

        // Publish Kafka event
        TaskEvent event = new TaskEvent(
                "TASK_CREATED",
                savedTask.getId(),
                savedTask.getTitle(),
                savedTask.getAssigneeId(),
                assignee.getName(),
                project.getId(),
                project.getName(),
                LocalDateTime.now());
        taskEventProducer.sendTaskEvent(event);

        return mapToResponse(savedTask, assignee.getName(), project.getName());
    }

    public Page<TaskResponse> getTasksByProject(Long projectId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasks = taskRepository.findByProjectId(projectId, pageable);

        return tasks.map(task -> {
            String assigneeName = "Unknown";
            String projectName = task.getProject() != null ? task.getProject().getName() : "Unknown";

            try {
                UserDTO assignee = userServiceClient.getUserById(task.getAssigneeId());
                assigneeName = assignee.getName();
            } catch (Exception e) {
                log.error("Failed to fetch user details", e);
            }

            return mapToResponse(task, assigneeName, projectName);
        });
    }

    public TaskResponse getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        String assigneeName = "Unknown";
        String projectName = task.getProject() != null ? task.getProject().getName() : "Unknown";

        try {
            UserDTO assignee = userServiceClient.getUserById(task.getAssigneeId());
            assigneeName = assignee.getName();
        } catch (Exception e) {
            log.error("Failed to fetch user details", e);
        }

        return mapToResponse(task, assigneeName, projectName);
    }

    @Transactional
    public TaskResponse updateTask(Long taskId, TaskRequest request, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        // Fetch user details
        UserDTO assignee = userServiceClient.getUserById(request.getAssigneeId());

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setAssigneeId(request.getAssigneeId());
        task.setStatus(Status.valueOf(request.getStatus()));
        task.setPriority(Priority.valueOf(request.getPriority()));
        task.setDueDate(request.getDueDate());

        Task updatedTask = taskRepository.save(task);

        // Publish Kafka event
        TaskEvent event = new TaskEvent(
                "TASK_UPDATED",
                updatedTask.getId(),
                updatedTask.getTitle(),
                updatedTask.getAssigneeId(),
                assignee.getName(),
                task.getProject().getId(),
                task.getProject().getName(),
                LocalDateTime.now());
        taskEventProducer.sendTaskEvent(event);

        return mapToResponse(updatedTask, assignee.getName(), task.getProject().getName());
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, String status, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        String oldStatus = task.getStatus().name();
        task.setStatus(Status.valueOf(status));
        Task updatedTask = taskRepository.save(task);

        // Fetch user details
        UserDTO assignee = userServiceClient.getUserById(task.getAssigneeId());

        // If task is completed, send TASK_COMPLETED event
        if ("DONE".equals(status)) {
            TaskEvent event = new TaskEvent(
                    "TASK_COMPLETED",
                    updatedTask.getId(),
                    updatedTask.getTitle(),
                    updatedTask.getAssigneeId(),
                    assignee.getName(),
                    task.getProject().getId(),
                    task.getProject().getName(),
                    LocalDateTime.now());
            taskEventProducer.sendTaskEvent(event);
        }

        return mapToResponse(updatedTask, assignee.getName(), task.getProject().getName());
    }

    @Transactional
    public void deleteTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        taskRepository.delete(task);
    }

    public List<TaskResponse> getMyTasks(Long userId, String status) {
        List<Task> tasks;

        if (status != null && !status.isEmpty()) {
            tasks = taskRepository.findByAssigneeIdAndStatus(userId, status);
        } else {
            tasks = taskRepository.findByAssigneeId(userId);
        }

        return tasks.stream().map(task -> {
            String assigneeName = "Unknown";
            String projectName = task.getProject() != null ? task.getProject().getName() : "Unknown";

            try {
                UserDTO assignee = userServiceClient.getUserById(task.getAssigneeId());
                assigneeName = assignee.getName();
            } catch (Exception e) {
                log.error("Failed to fetch user details", e);
            }

            return mapToResponse(task, assigneeName, projectName);
        }).collect(Collectors.toList());
    }

    public List<TaskResponse> getOverdueTasks() {
        List<Task> tasks = taskRepository.findOverdueTasks();

        return tasks.stream().map(task -> {
            String assigneeName = "Unknown";
            String projectName = task.getProject() != null ? task.getProject().getName() : "Unknown";

            try {
                UserDTO assignee = userServiceClient.getUserById(task.getAssigneeId());
                assigneeName = assignee.getName();
            } catch (Exception e) {
                log.error("Failed to fetch user details", e);
            }

            return mapToResponse(task, assigneeName, projectName);
        }).collect(Collectors.toList());
    }

    private TaskResponse mapToResponse(Task task, String assigneeName, String projectName) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setAssigneeId(task.getAssigneeId());
        response.setAssigneeName(assigneeName);
        // Convert enum to String
        response.setStatus(task.getStatus().name());
        response.setPriority(task.getPriority().name());
        response.setDueDate(task.getDueDate());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        response.setProjectId(task.getProject() != null ? task.getProject().getId() : null);
        response.setProjectName(projectName);
        return response;
    }
}