package com.example.task_manager.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Key;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.task_manager.controller.CommentController;
import com.example.task_manager.dto.comment.CreateCommentDto;
import com.example.task_manager.exception.AccessDeniedException;
import com.example.task_manager.exception.handler.ExceptionHandlerController;
import com.example.task_manager.service.CommentService;
import com.example.task_manager.unit.util.JwtUtilTest;
import com.example.task_manager.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CommentController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommentControllerTest {
    
    private final String EMAIL = "test@mail.ru";
    private final String ADMIN_ROLE = "ADMIN";
    private final String USER_ROLE = "USER";
    
    @MockitoBean
    private CommentService commentService;
    @MockitoBean
    private JwtUtil jwtUtil;

    private MockMvc mockMvc;
    
    private Key secretKey;
    private String adminToken;
    private String userToken;
    
    private CreateCommentDto createCommentDto;

    @BeforeEach
    void setUp() {

        mockMvc = standaloneSetup(new CommentController(commentService))
                .setControllerAdvice(ExceptionHandlerController.class).build();

        secretKey = JwtUtilTest.generateSecretKey();
        adminToken = JwtUtilTest.generateTestToken(EMAIL, ADMIN_ROLE, secretKey);
        userToken = JwtUtilTest.generateTestToken(EMAIL, USER_ROLE, secretKey);

        createCommentDto = CreateCommentDto.builder()
            .comment("qwertyyu")
            .taskId(1)
            .build();
        
    }

    @Test
    @DisplayName("If data has been successfully insert then return ok")
    void createComment_shouldReturnOk() throws Exception{

        mockMvc.perform(
            post("/auth/user/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", adminToken)
                .content(asJsonString(createCommentDto)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("If user not admin or not assigned to this task then return forbidden")
    void createComment_shouldReturnForbidden() throws Exception{

        doThrow(new AccessDeniedException(Integer.toString(HttpStatus.UNAUTHORIZED.value())))
                .when(commentService).createComment(any(CreateCommentDto.class), anyString());

        mockMvc.perform(
            post("/auth/user/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", userToken)
                .content(asJsonString(createCommentDto)))
            .andExpect(status().isForbidden());
    }

    private static String asJsonString(final Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }
}
