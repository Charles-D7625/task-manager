package com.example.task_manager.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.task_manager.dto.userprofile.UserInfoDto;
import com.example.task_manager.dto.userprofile.UserRegisterDto;
import com.example.task_manager.entity.Role;
import com.example.task_manager.entity.UserProfile;
import com.example.task_manager.entity.constants.Roles;
import com.example.task_manager.exception.ExistingEntityException;
import com.example.task_manager.repository.UserProfileRepository;
import com.example.task_manager.service.impl.UserProfileServiceImpl;
import com.example.task_manager.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

    private final String EMAIL1 = "email1@mail.ru";
    private final String EMAIL2 = "email2@mail.ru";
    private final UUID USER_ID = UUID.randomUUID();
    private final String PASSWORD = "pass";

    @InjectMocks
    private UserProfileServiceImpl userProfileService;
    @Mock
    private UserProfileRepository userProfileRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    private UserRegisterDto userRegisterDto;
    private UserProfile userProfile1;
    private UserProfile userProfile2;

    @BeforeEach
    void setUp() {

        userRegisterDto = UserRegisterDto.builder()
            .email(EMAIL1)
            .password(passwordEncoder.encode(PASSWORD))
            .name("name")
            .build();

        userProfile1 = UserProfile.builder()
            .id(USER_ID)
            .email(EMAIL1)
            .role(Role.builder().name(Roles.ADMIN).build())
            .password(passwordEncoder.encode(PASSWORD))
            .build();

        userProfile2 = UserProfile.builder()
            .id(USER_ID)
            .email(EMAIL2)
            .password(passwordEncoder.encode(PASSWORD))
            .build();
        
    }

    @Test
    @Description("If user successfully registered then return ok")
    void registerUser_shouldReturnOk() {

        userProfileService.registerUser(userRegisterDto);

        when(userProfileRepository.findByEmail(EMAIL1)).thenReturn(Optional.of(userProfile1));

        assertThat(userProfileRepository.findByEmail(userRegisterDto.getEmail())).isPresent();
    }

    @Test
    @Description("If user is already registered then return not found")
    void registerExistingUser_shouldReturnNotFound() {

        when(userProfileRepository.findByEmail(EMAIL1)).thenReturn(Optional.of(userProfile1));

        ThrowingCallable registerUserMethod = () -> userProfileService.registerUser(userRegisterDto);

        assertThatThrownBy(registerUserMethod)
            .isInstanceOf(ExistingEntityException.class);
    }

    @Test
    @DisplayName("Should return list of all users")
    void getAllUsers_shouldReturnUsers() {

        when(userProfileRepository.findAll()).thenReturn(List.of(userProfile1, userProfile2));

        List<UserProfile> result = userProfileService.getAllUsers();

        assertThat(result).containsExactly(userProfile1, userProfile2);

        verify(userProfileRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return email and role of user")
    void getUserEmailAndRoleByEmail_shouldReturnUserInfoDto() {
        // Given
        when(userProfileRepository.findByEmail(anyString())).thenReturn(Optional.of(userProfile1));

        // When
        UserInfoDto result = userProfileService.getUserEmailAndRoleByEmail(EMAIL1);

        // Then
        assertThat(result.getEmail()).isEqualTo(userProfile1.getEmail());
        assertThat(result.getRole()).isEqualTo(userProfile1.getRole().getName().getValue());

        verify(userProfileRepository, times(1)).findByEmail(EMAIL1);
    }

}
