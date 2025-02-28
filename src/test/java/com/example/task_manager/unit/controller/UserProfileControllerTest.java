package com.example.task_manager.unit.controller;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.task_manager.controller.UserProfileController;
import com.example.task_manager.dto.userprofile.UserInfoDto;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.UserProfile;
import com.example.task_manager.entity.constants.Roles;
import com.example.task_manager.exception.EntityNotFoundException;
import com.example.task_manager.exception.handler.ExceptionHandlerController;
import com.example.task_manager.service.UserProfileService;
import com.example.task_manager.unit.dto.UserProfileDto;
import com.example.task_manager.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserProfileController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserProfileControllerTest {

    private final String EMAIL = "user1@example.com";

    private MockMvc mockMvc;

    @MockitoBean
    private UserProfileService userProfileService;
    @MockitoBean
    private JwtUtil jwtUtil;

    private List<UserProfile> mockUsers;
    private UserInfoDto userInfoDto;

    @BeforeEach
    void setUp() {

        mockMvc = standaloneSetup(new UserProfileController(userProfileService))
            .setControllerAdvice(ExceptionHandlerController.class).build();

        UserProfile user1 = UserProfile.builder()
            .id(UUID.randomUUID())
            .name("User1")
            .email(EMAIL)
            .password("password1")
            .build();

        UserProfile user2 = UserProfile.builder()
            .id(UUID.randomUUID())
            .name("User2")
            .email(EMAIL)
            .password("password2")
            .build();

        mockUsers = Arrays.asList(user1, user2);

        userInfoDto = UserInfoDto.builder()
            .email(EMAIL)
            .role("USER")
            .build();
    }

    @Test
    @DisplayName("Should return list of users")
    void getUsers_shouldReturnOk() throws Exception{

        when(userProfileService.getAllUsers()).thenReturn(mockUsers);

        MvcResult mvcResult = mockMvc.perform(get("/auth/users"))
            .andExpect(status().isOk())
            .andReturn();

            verifyBody(asJsonString(mockUsers), mvcResult.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("If data has been successfully insert then return ok")
    void getUser_shouldReturnOk() throws Exception{

        when(userProfileService.getUserEmailAndRoleByEmail(EMAIL)).thenReturn(userInfoDto);

        MvcResult mvcResult = mockMvc.perform(get("/auth/user")
                .param("email", EMAIL))
            .andExpect(status().isOk())
            .andReturn();

            verifyBody(asJsonString(userInfoDto), mvcResult.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("If data has been successfully insert then return ok")
    void getUser_shouldReturnNotFound() throws Exception{

        when(userProfileService.getUserEmailAndRoleByEmail(EMAIL)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/auth/user")
                .param("email", EMAIL))
            .andExpect(status().isNotFound());

    }

    private String asJsonString(final Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }

    private void verifyBody(String expectedBody, String actualBody) {
        assertThat(actualBody).isEqualTo(expectedBody);
    }
}
