package com.example.task_manager.service;

import java.util.List;

import com.example.task_manager.dto.userprofile.UserInfoDto;
import com.example.task_manager.dto.userprofile.UserRegisterDto;
import com.example.task_manager.entity.UserProfile;

public interface UserProfileService {

    void registerUser(UserRegisterDto userProfile);

    List<UserProfile> getAllUsers();

    UserInfoDto getUserEmailAndRoleByEmail(String email);
}
