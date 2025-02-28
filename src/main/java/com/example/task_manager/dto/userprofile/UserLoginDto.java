package com.example.task_manager.dto.userprofile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.ToString;

import jakarta.validation.constraints.NotBlank;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PUBLIC)
@ToString
public class UserLoginDto {

    @NotBlank (
        message = "Field cannot be empty"
    )
    String email;
    
    @NotBlank (
        message = "Field cannot be empty"
    )
    String password;
}
