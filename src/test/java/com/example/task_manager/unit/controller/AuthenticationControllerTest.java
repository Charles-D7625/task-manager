package com.example.task_manager.unit.controller;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.task_manager.controller.AuthenticationController;
import com.example.task_manager.dto.userprofile.UserLoginDto;
import com.example.task_manager.dto.userprofile.UserRegisterDto;
import com.example.task_manager.exception.ExistingEntityException;
import com.example.task_manager.exception.handler.ExceptionHandlerController;
import com.example.task_manager.service.AuthenticationService;
import com.example.task_manager.service.UserProfileService;
import com.example.task_manager.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthenticationController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationControllerTest {

    @MockitoBean
    private AuthenticationService authenticationService;
    @MockitoBean
    private UserProfileService userProfileService;
    @MockitoBean
    private JwtUtil jwtUtil;

    private MockMvc mockMvc;

    private UserRegisterDto userRegisterDto;
    private UserLoginDto userLoginDto;

    private final String ADMIN_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWFpbEBtYWlsLnJ1IiwiaWQiOiJjN2E2NzNjYS1hNWYxLTRhNmItOTg0YS1jNWFmZWE0MGQ5YjEiLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE3NDA2OTk5OTksImV4cCI6MTc0MDc4NjM5OX0.qBKsRCf-hkY5c_Kk3JPpGBP7KwqMRzJ9dbSRCkF7KnQ";
    private final String EMAIL = "test@mail.ru";

    @BeforeEach
    void setUp() {

        mockMvc = standaloneSetup(new AuthenticationController(authenticationService, userProfileService))
                .setControllerAdvice(ExceptionHandlerController.class).build();

        userRegisterDto = UserRegisterDto.builder()
            .email(EMAIL)
            .password("123")
            .name("Name")
            .build();

        userLoginDto = UserLoginDto.builder()
            .email(EMAIL)
            .password("123")
            .build();

    }

    @Test
    @DisplayName("If user has been successfully registered then return ok")
    void registerUser_shouldReturnOk() throws Exception {

        mockMvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRegisterDto)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("If user already registered then return not_found")
    void registerUser_shouldReturnNotFound() throws Exception {

        doThrow(new ExistingEntityException(Integer.toString(HttpStatus.NOT_FOUND.value())))
            .when(userProfileService).registerUser(any(UserRegisterDto.class));

        mockMvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRegisterDto)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("If user login successfully then return ok")
    void loginUser_shouldReturnOk() throws Exception {

        when(jwtUtil.extractEmail(any())).thenReturn(EMAIL);

        MvcResult mvcResult = mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userLoginDto)))
            .andExpect(status().isOk())
            .andReturn();

        verifyBody(jwtUtil.extractEmail(mvcResult.getResponse().getContentAsString()), userLoginDto.getEmail());
    }

    private static String asJsonString(final Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }

    private void verifyBody(String expectedBody, String actualBody) {
        assertThat(actualBody).isEqualTo(expectedBody);
    }
}
