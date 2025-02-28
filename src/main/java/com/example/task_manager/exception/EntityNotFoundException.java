package com.example.task_manager.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {

        super(message);
    }
}
