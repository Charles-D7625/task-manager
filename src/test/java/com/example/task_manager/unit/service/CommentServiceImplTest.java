package com.example.task_manager.unit.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.example.task_manager.dto.comment.CreateCommentDto;
import com.example.task_manager.entity.Comment;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.UserProfile;
import com.example.task_manager.exception.AccessDeniedException;
import com.example.task_manager.exception.EntityNotFoundException;
import com.example.task_manager.repository.CommentRepository;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.service.impl.CommentServiceImpl;
import com.example.task_manager.util.JwtAuthenticationFilter;
import com.example.task_manager.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Mock
    private JwtUtil jwtUtil;

    @Captor
    private ArgumentCaptor<Comment> savedComment;

    private Comment comment;
    private Task task;
    private CreateCommentDto createCommentDto;

    private final String USER_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJydHllbWFpbEBtYWlsLnJ1IiwiaWQiOiIwOWE4YTZiMC1mN2EzLTQwNDItYjlkMy0xN2NkOTUxYWE1NDUiLCJyb2xlIjoiVVNFUiIsImlhdCI6MTc0MDcwMDA0NywiZXhwIjoxNzQwNzg2NDQ3fQ.jsxwDupwSn7RD_Y4-BqDG-p5s2irEOOV9vxAMbNqnKg";
    private final String ADMIN_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWFpbEBtYWlsLnJ1IiwiaWQiOiJjN2E2NzNjYS1hNWYxLTRhNmItOTg0YS1jNWFmZWE0MGQ5YjEiLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE3NDA2OTk5OTksImV4cCI6MTc0MDc4NjM5OX0.qBKsRCf-hkY5c_Kk3JPpGBP7KwqMRzJ9dbSRCkF7KnQ";
    private final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-556642440000");

    @BeforeEach
    void setUp() {
        
        task = Task.builder()
            .id(1)
            .title("title")
            .priority("Высокий")
            .description("qwerty")
            .status("В ожидании")
            .userProfile(UserProfile.builder().id(USER_ID).build())
            .build();
        
        comment = Comment.builder()
            .id(1)
            .commentary("comment")
            .task(task)
            .build();

        createCommentDto = CreateCommentDto.builder()
            .comment("comment")
            .taskId(1)
            .build();

    }

    @Test
    @DisplayName("If comment successfully saved then return ok")
    void saveComment_ifCommentSaved_thenReturnOk() {

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
        when(jwtUtil.extractRoleFromToken(any())).thenReturn("ADMIN");

        commentService.createComment(createCommentDto, ADMIN_TOKEN);

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("If user hasn't rights to comment then return forbidden")
    void saveComment_ifNotAllowToSave_thenReturnForbidden() {

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
        when(jwtUtil.extractRoleFromToken(any())).thenReturn("USER");

        ThrowingCallable createCommentMethod = () -> commentService.createComment(createCommentDto, USER_TOKEN);

        assertThatThrownBy(createCommentMethod)
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("If task not found then return not found")
    void saveComment_ifTaskNotFound_thenReturnNotFound() {

        when(jwtUtil.extractRoleFromToken(any())).thenReturn("USER");

        ThrowingCallable createCommentMethod = () -> commentService.createComment(createCommentDto, USER_TOKEN);

        assertThatThrownBy(createCommentMethod)
            .isInstanceOf(EntityNotFoundException.class);
    }
}
