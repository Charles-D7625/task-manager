package com.example.task_manager.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.example.task_manager.entity.Role;
import com.example.task_manager.entity.UserProfile;
import com.example.task_manager.entity.constants.Roles;
import com.example.task_manager.exception.EntityNotFoundException;
import com.example.task_manager.repository.UserProfileRepository;
import com.example.task_manager.service.impl.AuthenticationServiceImpl;
import com.example.task_manager.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    private final UUID USER_ID = UUID.fromString("9b81ee52-2c0d-4bda-90b4-0b12e9d6f467");
    private final String EMAIL = "email";
    private final String PASSWORD = "123";
    private final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJydHllbWFpbEBtYWlsLnJ1IiwiaWQiOiIwOWE4YTZiMC1mN2EzLTQwNDItYjlkMy0xN2NkOTUxYWE1NDUiLCJyb2xlIjoiVVNFUiIsImlhdCI6MTc0MDU2MjI3NywiZXhwIjoxNzQwNjQ4Njc3fQ.SH-x5I76sE0JIfrfCnsJMly_PgTWTojLJMfwPr4_e-c";

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;
    @Mock
    private UserProfileRepository userProfileRepository;
    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    @Mock
    private JwtUtil jwtUtil;

    private UserProfile userProfile;

    @BeforeEach
    void setUp() {

        userProfile = UserProfile.builder()
            .id(USER_ID)
            .email(EMAIL)
            .password(passwordEncoder.encode(PASSWORD))
            .role(Role.builder().name(Roles.USER).build())
            .build();
    }

    @Test
    @DisplayName("if email and password correct then return token")
    void singIn_shouldReturnToken() {

        when(userProfileRepository.findByEmail(EMAIL)).thenReturn(Optional.of(userProfile));
        when(jwtUtil.generateToken(EMAIL, "USER", USER_ID)).thenReturn(ACCESS_TOKEN);

        String token = authenticationService.singIn(EMAIL, PASSWORD);

        assertThat(token).isEqualTo(ACCESS_TOKEN);
        verify(userProfileRepository, times(1)).findByEmail(EMAIL);
    }

    @Test
    @DisplayName("if email and password incorrect then return not_found")
    void singIn_shouldReturnNotFound() {

        when(userProfileRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        ThrowingCallable signIn = () -> authenticationService.singIn(EMAIL, PASSWORD);

        assertThatThrownBy(signIn)
            .isInstanceOf(EntityNotFoundException.class);
    }

}
