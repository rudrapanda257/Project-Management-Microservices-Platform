package com.taskmanager.project.service;

import com.taskmanager.project.dto.TaskResponse;
import com.taskmanager.project.dto.TaskSearchCriteria;
import com.taskmanager.project.entity.Task;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskSearchService {

    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public Page<TaskResponse> searchTasks(TaskSearchCriteria criteria, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> query = cb.createQuery(Task.class);
        Root<Task> task = query.from(Task.class);

        // ✅ FIX: Add eager fetch to avoid lazy loading issues
        task.fetch("project", JoinType.LEFT);

        List<Predicate> predicates = buildPredicates(criteria, cb, task);

        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        // Add default ordering
        query.orderBy(cb.desc(task.get("createdAt")));

        // Create typed query
        TypedQuery<Task> typedQuery = entityManager.createQuery(query);

        // Apply pagination
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Task> results = typedQuery.getResultList();

        // Get total count
        long total = getTotalCount(criteria, cb);

        // ✅ FIX: Map to TaskResponse INSIDE the transaction
        List<TaskResponse> responseList = results.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responseList, pageable, total);
    }

    private List<Predicate> buildPredicates(TaskSearchCriteria criteria,
            CriteriaBuilder cb,
            Root<Task> task) {
        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getProjectId() != null) {
            predicates.add(cb.equal(task.get("project").get("id"), criteria.getProjectId()));
        }

        if (criteria.getAssigneeId() != null) {
            predicates.add(cb.equal(task.get("assigneeId"), criteria.getAssigneeId()));
        }

        if (criteria.getStatus() != null) {
            predicates.add(cb.equal(task.get("status"), criteria.getStatus()));
        }

        if (criteria.getPriority() != null) {
            predicates.add(cb.equal(task.get("priority"), criteria.getPriority()));
        }

        if (criteria.getDueDateFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(task.get("dueDate"), criteria.getDueDateFrom()));
        }

        if (criteria.getDueDateTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(task.get("dueDate"), criteria.getDueDateTo()));
        }

        return predicates;
    }

    private long getTotalCount(TaskSearchCriteria criteria, CriteriaBuilder cb) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Task> task = countQuery.from(Task.class);

        List<Predicate> predicates = buildPredicates(criteria, cb, task);

        countQuery.select(cb.count(task));

        if (!predicates.isEmpty()) {
            countQuery.where(predicates.toArray(new Predicate[0]));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    // ✅ NEW: Add mapping method
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