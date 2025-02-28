package com.example.task_manager.entity.constants;

import com.example.task_manager.exception.IllegalArgumentException;

public enum Priority {

    HIGH("Высокий"),
    MIJOR("Средний"),
    MINOR("Низкий");

    private final String value;

    Priority(String value) {

        this.value = value;
    }

    public static Priority fromValue(String value) {

        for (Priority priority : Priority.values()) {
            if (priority.getValue().equals(value)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    public String getValue() {

        return value;
    }
}
