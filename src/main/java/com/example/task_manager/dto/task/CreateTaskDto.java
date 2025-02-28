package com.example.task_manager.dto.task;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.ToString;

import java.util.UUID;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PUBLIC)
@ToString
public class CreateTaskDto {

    @NotBlank (
        message = "Title должен быть заполнен"
    )
    String title;
    @NotBlank (
        message = "Priority должен быть заполнен"
    )
    String priority;
    String status;
    String description;
    UUID assignedUser;
}
