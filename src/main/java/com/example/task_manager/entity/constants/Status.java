package com.example.task_manager.entity.constants;

import com.example.task_manager.exception.IllegalArgumentException;

public enum Status {

    WAITING("В ожидании"),
    INPROGRESS("В процессе"),
    DONE("Завершено");

    private final String value;

    Status(String value) {

        this.value = value;
    }

    public static Status fromValue(String value) {

        for (Status status : Status.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    public String getValue() {

        return value;
    }
}
