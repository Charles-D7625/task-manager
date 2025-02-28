package com.example.task_manager.entity.constants;

public enum Roles {

    ADMIN("ADMIN"),
    USER("USER"),
    VISITOR("VISITOR");

    private final String value;

    Roles(String value) {
        this.value = value;
    }

    public String getValue() {
        
        return value;
    }
}
