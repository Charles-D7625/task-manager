package com.example.task_manager.service;

import java.util.List;

import com.example.task_manager.dto.task.CreateTaskDto;
import com.example.task_manager.dto.task.UpdateTaskDto;
import com.example.task_manager.entity.Task;

public interface TaskService {

    void createTask(CreateTaskDto createTaskDto, String authHeader); //admin

    List<Task> getAllTasks(int page, int size); //user
        
    List<Task> getTasksByAuthorId(String authorId, int page, int size); //user

    List<Task> getTasksByAssignedUserId(String assignedUserId, int page, int size); //user

    Task getTask(int taskId); //user

    void deleteTask(int taskId); //admin

    void updateTask(UpdateTaskDto updateTaskDto, int taskId); // admin

    void changeTaskStatus(String status, int taskId); //user

}
