package com.taskmanager.project.repository;

import com.taskmanager.project.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByTaskId(Long taskId);

    List<Attachment> findByUploadedBy(Long userId);
}