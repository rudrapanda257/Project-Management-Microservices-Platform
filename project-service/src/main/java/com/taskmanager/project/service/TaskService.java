package com.taskmanager.project.service;

import com.taskmanager.project.dto.TaskRequest;
import com.taskmanager.project.dto.TaskResponse;
import com.taskmanager.project.entity.Project;
import com.taskmanager.project.entity.Task;
import com.taskmanager.project.exception.ProjectNotFoundException;
import com.taskmanager.project.exception.TaskNotFoundException;
import com.taskmanager.project.exception.UnauthorizedException;
import com.taskmanager.project.repository.ProjectRepository;
import com.taskmanager.project.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    
    @Transactional
    public TaskResponse createTask(Long projectId, TaskRequest request, Long userId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException(projectId));
        
        if (!project.getOwnerId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to add tasks to this project");
        }
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setAssigneeId(request.getAssigneeId());
        task.setStatus(Task.TaskStatus.valueOf(request.getStatus()));
        task.setPriority(Task.TaskPriority.valueOf(request.getPriority()));
        task.setDueDate(request.getDueDate());
        task.setProject(project);
        
        Task savedTask = taskRepository.save(task);
        return mapToResponse(savedTask);
    }
    
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByProject(Long projectId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return taskRepository.findByProjectId(projectId, pageable).map(this::mapToResponse);
    }
    
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
        return mapToResponse(task);
    }
    
    @Transactional
    public TaskResponse updateTask(Long taskId, TaskRequest request, Long userId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
        
        if (!task.getProject().getOwnerId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this task");
        }
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setAssigneeId(request.getAssigneeId());
        task.setStatus(Task.TaskStatus.valueOf(request.getStatus()));
        task.setPriority(Task.TaskPriority.valueOf(request.getPriority()));
        task.setDueDate(request.getDueDate());
        
        Task updatedTask = taskRepository.save(task);
        return mapToResponse(updatedTask);
    }
    
    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, String status, Long userId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
        
        if (!task.getProject().getOwnerId().equals(userId) && !task.getAssigneeId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this task status");
        }
        
        task.setStatus(Task.TaskStatus.valueOf(status));
        Task updatedTask = taskRepository.save(task);
        return mapToResponse(updatedTask);
    }
    
    @Transactional
    public void deleteTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
        
        if (!task.getProject().getOwnerId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this task");
        }
        
        taskRepository.delete(task);
    }
    
    @Transactional(readOnly = true)
    public List<TaskResponse> getMyTasks(Long userId, String status) {
        List<Task> tasks;
        if (status != null && !status.isEmpty()) {
            tasks = taskRepository.findByAssigneeIdAndStatus(userId, Task.TaskStatus.valueOf(status));
        } else {
            tasks = taskRepository.findByAssigneeId(userId);
        }
        return tasks.stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasks() {
        return taskRepository.findOverdueTasks().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
            .id(task.getId())
            .title(task.getTitle())
            .description(task.getDescription())
            .assigneeId(task.getAssigneeId())
            .status(task.getStatus().name())
            .priority(task.getPriority().name())
            .dueDate(task.getDueDate())
            .createdAt(task.getCreatedAt())
            .updatedAt(task.getUpdatedAt())
            .projectId(task.getProject().getId())
            .projectName(task.getProject().getName())
            .build();
    }
}