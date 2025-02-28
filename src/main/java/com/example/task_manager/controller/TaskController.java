package com.example.task_manager.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.task_manager.dto.task.CreateTaskDto;
import com.example.task_manager.dto.task.UpdateTaskDto;
import com.example.task_manager.entity.Task;
import com.example.task_manager.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Task Controller", description = "Task Controller")
public class TaskController {

    private final TaskService taskService;

    //получить все задачи, которые есть в бд
    @Operation(
        summary = "Returns all tasks",
        description = "Allows to return all tasks with pagination"
    )
    @GetMapping("/user/tasks")
    public ResponseEntity<List<Task>> getAllTasks(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size)
    {

        List<Task> tasks = taskService.getAllTasks(page, size);

        return ResponseEntity.ok(tasks);
    }

    //получить все задачи по автору
    @Operation(
        summary = "Returns tasks by author user",
        description = "Allows to return author tasks with pagination"
    )
    @GetMapping(path = "/user/tasks/author")
    public ResponseEntity<List<Task>> getTasksByAuthorId(@RequestParam String authorId,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "5") int size) 
    {

        List<Task> tasks = taskService.getTasksByAuthorId(authorId, page, size);

        return ResponseEntity.ok(tasks);
    }

    //получить все задачи по назначенному
    @Operation(
        summary = "Returns tasks by assigned user",
        description = "Allows to return assigned user tasks with pagination"
    )
    @GetMapping(path = "/user/tasks/assigned")
    public ResponseEntity<List<Task>> getTasksByAssignedUserId(@RequestParam String assignedUserId,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "5") int size) {

        List<Task> tasks = taskService.getTasksByAssignedUserId(assignedUserId, page, size);

        return ResponseEntity.ok(tasks);
    }

    //получить задачу
    //!!!не забыть разграничить для изменение статусов
    @Operation(
        summary = "Returns task by task id",
        description = "Allows to return task with pagination"
    )
    @GetMapping("/user/task")
    public ResponseEntity<Task> getTask(@RequestParam Integer taskId, 
                                        @RequestHeader("Authorization") String authHeader) {

        Task task = taskService.getTask(taskId);

        return ResponseEntity.ok(task);
    }
    
    //поменять статус задачи юзера к которой он привязан
    @Operation(
        summary = "Update task by task id",
        description = "Allows to update task status"
    )
    @PatchMapping("/user/task")
    public ResponseEntity<Void> changeTaskStatus(@RequestBody String status, @RequestParam Integer taskId) {

        taskService.changeTaskStatus(status, taskId);

        return ResponseEntity.ok().build();
    }

    //поменять статус задачи
    //поменять приоритет задачи
    //поменять описание задачи
    //поменять заголовок задачи
    @Operation(
        summary = "Update task by task id",
        description = "Allows to update task status, priority, title and description"
    )
    @PatchMapping("/task")
    public ResponseEntity<Void> updateTask(@RequestBody UpdateTaskDto updateTaskDto, @RequestParam Integer taskId) {

        taskService.updateTask(updateTaskDto, taskId);

        return ResponseEntity.ok().build();
    }

    //создать задачу
    @Operation(
        summary = "Create task",
        description = "Allows to create task"
    )
    @PostMapping("/task")
    public ResponseEntity<CreateTaskDto> createTask(@RequestBody CreateTaskDto createTaskDto,
                                                    @RequestHeader("Authorization") String authHeader) {
        
        taskService.createTask(createTaskDto, authHeader);
        
        return ResponseEntity.ok().build();
    }
    
    //удалить задачу
    @Operation(
        summary = "Delete task",
        description = "Allows to delete task by task id"
    )
    @DeleteMapping("/task")
    public ResponseEntity<Void> deleteTask(@RequestParam Integer taskId) {

        taskService.deleteTask(taskId);

        return ResponseEntity.ok().build();
    }
}
