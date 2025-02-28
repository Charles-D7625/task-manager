package com.example.task_manager.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.task_manager.dto.task.CreateTaskDto;
import com.example.task_manager.dto.task.UpdateTaskDto;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.UserProfile;
import com.example.task_manager.entity.constants.Status;
import com.example.task_manager.exception.EntityNotFoundException;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.UserProfileRepository;
import com.example.task_manager.service.impl.TaskServiceImpl;
import com.example.task_manager.util.JwtAuthenticationFilter;
import com.example.task_manager.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    private final int PAGE = 0;
    private final int SIZE = 5;
    private final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJydHllbWFpbEBtYWlsLnJ1IiwiaWQiOiIwOWE4YTZiMC1mN2EzLTQwNDItYjlkMy0xN2NkOTUxYWE1NDUiLCJyb2xlIjoiVVNFUiIsImlhdCI6MTc0MDU2MjI3NywiZXhwIjoxNzQwNjQ4Njc3fQ.SH-x5I76sE0JIfrfCnsJMly_PgTWTojLJMfwPr4_e-c";
    private final String AUTHOR_ID = "9b81ee52-2c0d-4bda-90b4-0b12e9d6f467";
    private final UUID USER_ID = UUID.fromString("9b81ee52-2c0d-4bda-90b4-0b12e9d6f467");
    private final String ASSIGNED_USER_ID = "9b81ee52-2c0d-4bda-90b4-0b12e9d6f467";
    private final Pageable PAGEABLE = PageRequest.of(PAGE, SIZE);

    @InjectMocks
    private TaskServiceImpl taskService;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserProfileRepository userProfileRepository;
    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Mock
    private JwtUtil jwtUtil;

    
    @Captor
    private ArgumentCaptor<Task> savedTask;
    private UserProfile userProfile;
    private Task task1;
    private Task task2;
    private CreateTaskDto createTaskDto;
    private UpdateTaskDto updateTaskDto;

    @BeforeEach
    void setUp() {

        userProfile = UserProfile.builder()
            .id(USER_ID)
            .email("email")
            .password("123")
            .build();

        task1 = Task.builder()
            .id(1)
            .title("Task 1")
            .priority("Высокий")
            .build();

        task2 = Task.builder()
            .id(2)
            .title("Task 2")
            .priority("Низкий")
            .build();

        createTaskDto = CreateTaskDto.builder()
            .title("title")
            .priority("Высокий")
            .status("Завершено")
            .description("desc")
            .assignedUser(UUID.fromString(ASSIGNED_USER_ID))
            .build();

        updateTaskDto = UpdateTaskDto.builder()
            .title("title")
            .priority("Высокий")
            .status("Завершено")
            .description("desc")
            .assignedUser(ASSIGNED_USER_ID)
            .build();


        
    }

    @Test
    @Description("Should successfully create a task")
    void createTask_shouldSaveTask() {

        when(jwtAuthenticationFilter.getUserIdFromJwt(ACCESS_TOKEN)).thenReturn(USER_ID);
        when(userProfileRepository.findById(USER_ID)).thenReturn(Optional.of(userProfile));
        
        taskService.createTask(createTaskDto, ACCESS_TOKEN);
        
        verify(taskRepository, times(1)).save(savedTask.capture());

        verifyThatCreatedTask(savedTask.getValue());
    }

    @Test
    @Description("Should return paginated list of tasks")
    void getAllTasks_shouldReturnPaginatedTasks() {
        List<Task> tasks = List.of(task1, task2);

        Page<Task> pagedTasks = new PageImpl<>(tasks, PAGEABLE, tasks.size());

        when(taskRepository.findAll(PAGEABLE)).thenReturn(pagedTasks);

        List<Task> result = taskService.getAllTasks(PAGE, SIZE);

        verify(taskRepository, times(1)).findAll(PAGEABLE);

        assertThat(result).containsExactly(task1, task2);
    }

    @Test
    @DisplayName("Should return task when it exists")
    void getTask_whenTaskExists_shouldReturnTask() {

        when(taskRepository.findById(1)).thenReturn(Optional.of(task1));

        Task result = taskService.getTask(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when task does not exist")
    void getTask_whenTaskDoesNotExist_shouldThrowException() {

        when(taskRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTask(1))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Should delete task when it exists")
    void deleteTask_whenTaskExists_shouldDeleteTask() {
        // Given
        when(taskRepository.findById(1)).thenReturn(Optional.of(task1));

        // When
        taskService.deleteTask(1);

        // Then
        verify(taskRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when task does not exist")
    void deleteTask_whenTaskDoesNotExist_shouldThrowException() {
        // Given
        when(taskRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.deleteTask(1))
            .isInstanceOf(EntityNotFoundException.class);

        verify(taskRepository, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("Should change task status when task exists")
    void changeTaskStatus_whenTaskExists_shouldChangeStatus() {
        // Given
        when(taskRepository.findById(1)).thenReturn(Optional.of(task1));

        // When
        taskService.changeTaskStatus("INPROGRESS", 1);

        // Then
        verify(taskRepository).save(task1);
        assert task1.getStatus().equals(Status.INPROGRESS.getValue());
    }


    
    @Test
    @DisplayName("Should update task fields when task exists")
    void updateTask_whenTaskExists_shouldUpdateFields() {

        when(taskRepository.findById(1)).thenReturn(Optional.of(task1));

        taskService.updateTask(updateTaskDto, 1);

        verify(taskRepository).save(task1);
        verifyThatUpdatedTask(task1);
    }

    @Test
    @DisplayName("Should return list of tasks by author id")
    void getTasksByAuthorId_shouldReturnTasks() {

        when(taskRepository.findByUserProfile_Id(UUID.fromString(AUTHOR_ID), PAGEABLE))
            .thenReturn(List.of(task1, task2));

        List<Task> result = taskService.getTasksByAuthorId(AUTHOR_ID, PAGE, SIZE);

        assertThat(result).containsExactly(task1, task2);

        verify(taskRepository, times(1)).findByUserProfile_Id(UUID.fromString(AUTHOR_ID), PAGEABLE);
    }

    @Test
    @DisplayName("Should return list of tasks by assigned user id")
    void getTasksByAssignedUserId_shouldReturnTasks() {

        when(taskRepository.findByAssignedUser(UUID.fromString(ASSIGNED_USER_ID), PAGEABLE))
            .thenReturn(List.of(task1, task2));

        List<Task> result = taskService.getTasksByAssignedUserId(UUID.fromString(ASSIGNED_USER_ID).toString(), PAGE, SIZE);

        assertThat(result).containsExactly(task1, task2);

        verify(taskRepository, times(1)).findByAssignedUser(UUID.fromString(ASSIGNED_USER_ID), PAGEABLE);
    }

    private void verifyThatUpdatedTask(Task task) {

        assert task.getTitle().equals(updateTaskDto.getTitle());
        assert task.getPriority().equals(updateTaskDto.getPriority());
        assert task.getStatus().equals(updateTaskDto.getStatus());
        assert task.getDescription().equals(updateTaskDto.getDescription());
        assert task.getAssignedUser().toString().equals(updateTaskDto.getAssignedUser());
    }

    private void verifyThatCreatedTask(Task task) {

        assert task.getTitle().equals(createTaskDto.getTitle());
        assert task.getPriority().equals(createTaskDto.getPriority());
        assert task.getStatus().equals(createTaskDto.getStatus());
        assert task.getUserProfile().equals(userProfile);
        assert task.getDescription().equals(createTaskDto.getDescription());
        assert task.getAssignedUser().equals(createTaskDto.getAssignedUser());
    }
}
