package com.example.task_manager.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {

        super(message);
    }
}
