package com.example.task_manager.dto.userprofile;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PUBLIC)
@ToString
public class UserRegisterDto {

    @NotBlank (
        message = "Field cannot be empty"
    )
    String email;
    
    @NotBlank (
        message = "Field cannot be empty"
    )
    String password;
    String name;
}
