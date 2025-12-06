package com.taskmanager.project.service;

import com.taskmanager.project.dto.ProjectRequest;
import com.taskmanager.project.dto.ProjectResponse;
import com.taskmanager.project.entity.Project;
import com.taskmanager.project.exception.ProjectNotFoundException;
import com.taskmanager.project.exception.UnauthorizedException;
import com.taskmanager.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    
    @Transactional
    public ProjectResponse createProject(ProjectRequest request, Long userId) {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setOwnerId(userId);
        
        Project savedProject = projectRepository.save(project);
        return mapToResponse(savedProject);
    }
    
    @Transactional(readOnly = true)
    public Page<ProjectResponse> getAllProjects(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return projectRepository.findAll(pageable).map(this::mapToResponse);
    }
    
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ProjectNotFoundException(id));
        return mapToResponse(project);
    }
    
    @Transactional(readOnly = true)
    public Page<ProjectResponse> getMyProjects(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return projectRepository.findByOwnerId(userId, pageable).map(this::mapToResponse);
    }
    
    @Transactional
    public ProjectResponse updateProject(Long id, ProjectRequest request, Long userId) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ProjectNotFoundException(id));
        
        if (!project.getOwnerId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this project");
        }
        
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        
        Project updatedProject = projectRepository.save(project);
        return mapToResponse(updatedProject);
    }
    
    @Transactional
    public void deleteProject(Long id, Long userId) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ProjectNotFoundException(id));
        
        if (!project.getOwnerId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this project");
        }
        
        projectRepository.delete(project);
    }
    
    private ProjectResponse mapToResponse(Project project) {
        return ProjectResponse.builder()
            .id(project.getId())
            .name(project.getName())
            .description(project.getDescription())
            .ownerId(project.getOwnerId())
            .createdAt(project.getCreatedAt())
            .taskCount(project.getTasks() != null ? project.getTasks().size() : 0)
            .build();
    }
}