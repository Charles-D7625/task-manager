package com.example.task_manager.unit.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.security.Key;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.task_manager.controller.TaskController;
import com.example.task_manager.dto.task.CreateTaskDto;
import com.example.task_manager.dto.task.UpdateTaskDto;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.UserProfile;
import com.example.task_manager.exception.handler.ExceptionHandlerController;
import com.example.task_manager.service.TaskService;
import com.example.task_manager.unit.util.JwtUtilTest;
import com.example.task_manager.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TaskController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaskControllerTest {

    private final int PAGE = 0;
    private final int SIZE = 5;
    private final UUID AUTHOR_ID = UUID.randomUUID();
    private final String EMAIL = "test@mail.ru";
    private final String ADMIN_ROLE = "ADMIN";
    private final String USER_ROLE = "USER";

    @MockitoBean
    private TaskService taskService;
    @MockitoBean
    private JwtUtil jwtUtil;

    private MockMvc mockMvc;

    private UpdateTaskDto updateTaskDto;
    private CreateTaskDto createTaskDto;

    private List<Task> mockTasks;
    private Task task1;
    private Task task2;

    private Key secretKey;
    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {

        mockMvc = standaloneSetup(new TaskController(taskService))
            .setControllerAdvice(ExceptionHandlerController.class).build();

        task1 = Task.builder()
            .id(1)
            .title("title")
            .status("Завершено")
            .userProfile(UserProfile.builder().id(AUTHOR_ID).build())
            .priority("Средний")
            .description("qwe")
            .assignedUser(AUTHOR_ID)
            .build();

        task2 = Task.builder()
            .id(2)
            .title("title2")
            .status("Завершено")
            .userProfile(UserProfile.builder().id(AUTHOR_ID).build())
            .priority("Средний")
            .description("qwe2")
            .assignedUser(AUTHOR_ID)
            .build();

        mockTasks = Arrays.asList(task1, task2);

        updateTaskDto = UpdateTaskDto.builder()
            .title("title")
            .status("status")
            .priority("priority")
            .description("qwe")
            .assignedUser(UUID.randomUUID().toString())
            .build();

        createTaskDto = CreateTaskDto.builder()
            .title("title")
            .status("status")
            .priority("priority")
            .description("qwe")
            .assignedUser(UUID.randomUUID())
            .build();

        secretKey = JwtUtilTest.generateSecretKey();
        adminToken = JwtUtilTest.generateTestToken(EMAIL, ADMIN_ROLE, secretKey);
        userToken = JwtUtilTest.generateTestToken(EMAIL, USER_ROLE, secretKey);
    }

    @Test
    @DisplayName("Return all tasks from db")
    void getAllTasks_ShouldReturnListOfTasks() throws Exception {

        when(taskService.getAllTasks(PAGE, SIZE)).thenReturn(mockTasks);

        MvcResult mvcResult = mockMvc.perform(get("/auth/user/tasks"))
            .andExpect(status().isOk())
            .andReturn();

        verifyBody(asJsonString(mockTasks), mvcResult.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Return all tasks by author id")
    void getTasksByAuthorId_ShouldReturnListOfTasks() throws Exception {

        when(taskService.getTasksByAuthorId(AUTHOR_ID.toString(), PAGE, SIZE)).thenReturn(mockTasks);

        MvcResult mvcResult = mockMvc.perform(get("/auth/user/tasks/author")
            .param("authorId", AUTHOR_ID.toString()))
            .andExpect(status().isOk())
            .andReturn();

        verifyBody(asJsonString(mockTasks), mvcResult.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Return all tasks by assigned user")
    void getTasksByAssignedUserId_ShouldReturnListOfTasks() throws Exception {

        when(taskService.getTasksByAssignedUserId(AUTHOR_ID.toString(), PAGE, SIZE)).thenReturn(mockTasks);

        MvcResult mvcResult = mockMvc.perform(get("/auth/user/tasks/assigned")
            .param("assignedUserId", AUTHOR_ID.toString()))
            .andExpect(status().isOk())
            .andReturn();

        verifyBody(asJsonString(mockTasks), mvcResult.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Should return task by task id")
    void getTask_ShouldReturnTask() throws Exception {

        when(taskService.getTask(1)).thenReturn(task1);
        
        MvcResult mvcResult = mockMvc.perform(get("/auth/user/task")
            .param("taskId", Integer.toString(1))
            .header("Authorization", adminToken))
            .andExpect(status().isOk())
            .andReturn();
        
        verifyBody(asJsonString(task1), mvcResult.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Update task status by id should return ok")
    void changeTaskStatus_ShouldReturnOk() throws Exception {

        mockMvc.perform(patch("/auth/user/task")
            .param("taskId", Integer.toString(1))
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString("Завершено")))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Update task status, priority, title or description and should return ok")
    void updateTask_ShouldReturnOk() throws Exception {

        mockMvc.perform(patch("/auth/task")
            .param("taskId", Integer.toString(1))
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(updateTaskDto)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Create task should return ok")
    void createTask_ShouldReturnOk() throws Exception {

        mockMvc.perform(post("/auth/task")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(createTaskDto)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete task should return ok")
    void deleteTask_ShouldReturnOk() throws Exception {

        mockMvc.perform(delete("/auth/task")
            .param("taskId", Integer.toString(1)))
            .andExpect(status().isOk());
    }

    private static String asJsonString(final Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }

    private void verifyBody(String expectedBody, String actualBody) {
        assertThat(actualBody).isEqualTo(expectedBody);
    }
}
