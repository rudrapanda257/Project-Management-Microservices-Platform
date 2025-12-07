package com.taskmanager.project.exception;

public class AttachmentNotFoundException extends RuntimeException {

    public AttachmentNotFoundException(String message) {
        super(message);
    }

    public AttachmentNotFoundException(Long attachmentId) {
        super("Attachment not found with id: " + attachmentId);
    }
}