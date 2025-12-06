package com.taskmanager.project.controller;

import com.taskmanager.project.dto.ProjectRequest;
import com.taskmanager.project.dto.ProjectResponse;
import com.taskmanager.project.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Create a new project
     */
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        Long userId = getCurrentUserId();
        log.info("Request to create project by user: {}", userId);

        ProjectResponse response = projectService.createProject(request, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all projects with pagination
     */
    @GetMapping
    public ResponseEntity<Page<ProjectResponse>> getAllProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Request to get all projects");

        Page<ProjectResponse> projects = projectService.getAllProjects(page, size);
        return ResponseEntity.ok(projects);
    }

    /**
     * Get projects owned by current user
     */
    @GetMapping("/my")
    public ResponseEntity<Page<ProjectResponse>> getMyProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getCurrentUserId();
        log.info("Request to get my projects for user: {}", userId);

        Page<ProjectResponse> projects = projectService.getMyProjects(userId, page, size);
        return ResponseEntity.ok(projects);
    }

    /**
     * Get project by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        log.info("Request to get project by ID: {}", id);

        ProjectResponse project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    /**
     * Update project
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {
        Long userId = getCurrentUserId();
        log.info("Request to update project ID: {} by user: {}", id, userId);

        ProjectResponse response = projectService.updateProject(id, request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete project
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        log.info("Request to delete project ID: {} by user: {}", id, userId);

        projectService.deleteProject(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Extract current user ID from JWT token
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        // For Day 3, we'll use a fixed user ID of 1
        // In Day 7, we'll use Feign Client to fetch actual user ID from User Service
        return 1L; // Temporary: assumes user ID is 1
    }
}