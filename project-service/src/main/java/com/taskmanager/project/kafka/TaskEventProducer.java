package com.taskmanager.project.kafka;

import com.taskmanager.project.kafka.dto.TaskEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskEventProducer {

    private static final String TOPIC = "task-events";
    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;

    public void sendTaskEvent(TaskEvent event) {
        try {
            CompletableFuture<SendResult<String, TaskEvent>> future = kafkaTemplate.send(TOPIC,
                    event.getTaskId().toString(), event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Task event sent successfully: {} - Offset: {}",
                            event.getEventType(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send task event: {}", event.getEventType(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Error sending task event", e);
        }
    }
}