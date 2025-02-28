package com.example.task_manager.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.task_manager.dto.userprofile.UserRegisterDto;
import com.example.task_manager.dto.userprofile.UserLoginDto;
import com.example.task_manager.service.AuthenticationService;
import com.example.task_manager.service.UserProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller", description = "Authentication Controller")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserProfileService userProfileService;
    
    @Operation(
        summary = "Register user ",
        description = "Register user"
    )
    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody UserRegisterDto userProfile) {
        
        userProfileService.registerUser(userProfile);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Login",
        description = "Login to user account by email and password to receive a token"
    )
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDto userLoginDto) {
        
        String token = authenticationService.singIn(userLoginDto.getEmail(), userLoginDto.getPassword());

        return ResponseEntity.ok(token);
    }
    
}
