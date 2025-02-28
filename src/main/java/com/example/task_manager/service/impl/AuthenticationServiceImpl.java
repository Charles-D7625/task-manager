package com.example.task_manager.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.task_manager.entity.UserProfile;
import com.example.task_manager.exception.EntityNotFoundException;
import com.example.task_manager.repository.UserProfileRepository;
import com.example.task_manager.service.AuthenticationService;
import com.example.task_manager.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String singIn(String email, String password) {

        UserProfile userProfile = userProfileRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Wrong email or password"));

        if (!passwordEncoder.matches(password, userProfile.getPassword()))
            throw new EntityNotFoundException("Wrong email or password");

        return jwtUtil.generateToken(email, userProfile.getRole().getName().getValue(), userProfile.getId());
    }
}
