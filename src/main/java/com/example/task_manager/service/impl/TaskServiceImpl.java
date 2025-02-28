package com.example.task_manager.service.impl;

import java.util.List;
import static java.text.MessageFormat.format;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.task_manager.dto.task.CreateTaskDto;
import com.example.task_manager.dto.task.UpdateTaskDto;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.UserProfile;
import com.example.task_manager.entity.constants.Priority;
import com.example.task_manager.entity.constants.Status;
import com.example.task_manager.exception.EntityNotFoundException;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.UserProfileRepository;
import com.example.task_manager.service.TaskService;
import com.example.task_manager.util.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserProfileRepository userProfileRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public void createTask(CreateTaskDto createTaskDto, String authHeader) {
        
        UUID userProfileId = jwtAuthenticationFilter.getUserIdFromJwt(authHeader);

        UserProfile userProfile = userProfileRepository.findById(userProfileId)
            .orElseThrow(() -> new EntityNotFoundException(format("User with id {0} not found", userProfileId)));

        Task task = new Task();

        task.setTitle(createTaskDto.getTitle());
        task.setPriority(Priority.fromValue(createTaskDto.getPriority()).getValue());
        task.setStatus(Status.fromValue(createTaskDto.getStatus()).getValue());
        task.setUserProfile(userProfile);
        task.setDescription(createTaskDto.getDescription());
        task.setAssignedUser(createTaskDto.getAssignedUser());

        taskRepository.save(task);
    }

    public List<Task> getAllTasks(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return taskRepository.findAll(pageable).toList();
    }

    public Task getTask(int taskId) {

        return taskRepository.findById(taskId)
            .orElseThrow(() -> new EntityNotFoundException(format("Task with id {0} not found", taskId)));
    }

    public void deleteTask(int taskId) {

        if(!taskRepository.findById(taskId).isPresent())
            throw new EntityNotFoundException(format("Task with id {0} not found", taskId));

        taskRepository.deleteById(taskId);
    }

    public void changeTaskStatus(String status, int taskId) {
    
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new EntityNotFoundException(format("Task with id {0} not found", taskId)));

        task.setStatus(Status.valueOf(status).getValue());
        taskRepository.save(task);
    }

    public void updateTask(UpdateTaskDto updateTaskDto, int taskId) {

        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new EntityNotFoundException(format("Task with id {0} not found", taskId)));

        if (updateTaskDto.getTitle() != null) {
            task.setTitle(updateTaskDto.getTitle());
        }
        if (updateTaskDto.getDescription() != null) {
            task.setDescription(updateTaskDto.getDescription());
        }
        if (updateTaskDto.getStatus() != null) {
            task.setStatus(Status.fromValue(updateTaskDto.getStatus()).getValue());
        }
        if (updateTaskDto.getPriority() != null) {
            task.setPriority(Priority.fromValue(updateTaskDto.getPriority()).getValue());
        }
        if (updateTaskDto.getAssignedUser() != null) {
            task.setAssignedUser(UUID.fromString(updateTaskDto.getAssignedUser()));
        }

        taskRepository.save(task);
    }

    public List<Task> getTasksByAuthorId(String authorId, int page, int size) {
        
        Pageable pageable = PageRequest.of(page, size);

        return taskRepository.findByUserProfile_Id(UUID.fromString(authorId), pageable);
    }

    public List<Task> getTasksByAssignedUserId(String assignedUserId, int page, int size) {
        
        Pageable pageable = PageRequest.of(page, size);

        return taskRepository.findByAssignedUser(UUID.fromString(assignedUserId), pageable);
    }

}
