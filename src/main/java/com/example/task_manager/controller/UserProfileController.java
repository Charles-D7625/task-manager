package com.example.task_manager.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.task_manager.dto.userprofile.UserInfoDto;
import com.example.task_manager.entity.UserProfile;
import com.example.task_manager.service.UserProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "User Profile Controller", description = "User Profile Controller")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Operation(
        summary = "Return all users",
        description = "Return all users from db"
    )
    @GetMapping("/users")
    public ResponseEntity<List<UserProfile>> getAllUsers() {

        List<UserProfile> allUsers = userProfileService.getAllUsers();

        return ResponseEntity.ok(allUsers);
    }
    
    @Operation(
        summary = "Return user",
        description = "Return users email and his role"
    )
    @GetMapping("/user")
    public ResponseEntity<UserInfoDto> getCurrentUser(@RequestParam String email) {

        UserInfoDto user = userProfileService.getUserEmailAndRoleByEmail(email);

        return ResponseEntity.ok(user);
    }
}
