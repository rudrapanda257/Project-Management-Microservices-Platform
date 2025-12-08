package com.taskmanager.notification.kafka;

import com.taskmanager.notification.kafka.dto.TaskEvent;
import com.taskmanager.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskEventConsumer {
    
    private final NotificationService notificationService;
    
    @KafkaListener(topics = "task-events", groupId = "notification-service-group")
    public void consumeTaskEvent(TaskEvent event) {
        try {
            log.info("Received task event: {} for task: {}", event.getEventType(), event.getTaskTitle());
            
            String message = buildNotificationMessage(event);
            
            // Create notification for the assignee
            notificationService.createNotification(
                event.getAssigneeId(),
                message,
                event.getEventType()
            );
            
            log.info("Successfully processed task event: {}", event.getEventType());
            
        } catch (Exception e) {
            log.error("Error processing task event: {}", event.getEventType(), e);
        }
    }
    
    private String buildNotificationMessage(TaskEvent event) {
        switch (event.getEventType()) {
            case "TASK_CREATED":
                return String.format("You have been assigned to task: '%s' in project '%s'",
                    event.getTaskTitle(), event.getProjectName());
            
            case "TASK_UPDATED":
                return String.format("Task '%s' has been updated in project '%s'",
                    event.getTaskTitle(), event.getProjectName());
            
            case "TASK_COMPLETED":
                return String.format("Task '%s' has been marked as completed in project '%s'",
                    event.getTaskTitle(), event.getProjectName());
            
            default:
                return String.format("Task '%s' event: %s",
                    event.getTaskTitle(), event.getEventType());
        }
    }
}