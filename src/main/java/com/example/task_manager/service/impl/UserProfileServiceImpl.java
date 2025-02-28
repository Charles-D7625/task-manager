package com.example.task_manager.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.task_manager.dto.userprofile.UserInfoDto;
import com.example.task_manager.dto.userprofile.UserRegisterDto;
import com.example.task_manager.entity.UserProfile;
import com.example.task_manager.exception.EntityNotFoundException;
import com.example.task_manager.exception.ExistingEntityException;
import com.example.task_manager.repository.UserProfileRepository;
import com.example.task_manager.service.UserProfileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private final PasswordEncoder passwordEncoder;

    private final UserProfileRepository userProfileRepository;

    public void registerUser(UserRegisterDto userRegisterDto) {
        
        log.info("registerUser() is invoke...");

        if (userProfileRepository.findByEmail(userRegisterDto.getEmail()).isPresent()) {

            throw new ExistingEntityException("User with this email is already registered");
        }

        UserProfile userProfile = new UserProfile();

        userProfile.setEmail(userRegisterDto.getEmail());
        userProfile.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        userProfile.setName(userRegisterDto.getName());

        userProfileRepository.save(userProfile);
    }

    public List<UserProfile> getAllUsers() {

        return userProfileRepository.findAll();
    }

    public UserInfoDto getUserEmailAndRoleByEmail(String email) {
        
        UserProfile userProfile = userProfileRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User with this email not found"));

        String role = userProfile.getRole().getName().getValue();

        return UserInfoDto.builder()
            .email(email)
            .role(role)
            .build();
        
    }
   
}
