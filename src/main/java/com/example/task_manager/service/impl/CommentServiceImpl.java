package com.example.task_manager.service.impl;

import static java.text.MessageFormat.format;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.task_manager.dto.comment.CreateCommentDto;
import com.example.task_manager.entity.Comment;
import com.example.task_manager.entity.Task;
import com.example.task_manager.exception.AccessDeniedException;
import com.example.task_manager.exception.EntityNotFoundException;
import com.example.task_manager.repository.CommentRepository;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.service.CommentService;
import com.example.task_manager.util.JwtAuthenticationFilter;
import com.example.task_manager.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtUtil jwtUtil;

    public void createComment(CreateCommentDto createCommentDto, String authHeader) {
        
        UUID userProfileId = jwtAuthenticationFilter.getUserIdFromJwt(authHeader);
        String role = jwtUtil.extractRoleFromToken(authHeader.substring(7));

        Comment comment = new Comment();
        
        Task task = taskRepository.findById(createCommentDto.getTaskId())
            .orElseThrow(() -> new EntityNotFoundException(format("Task with id {0} not found", createCommentDto.getTaskId())));

        if((task.getUserProfile().getId() != userProfileId) && (!role.equals("ADMIN")))
            throw new AccessDeniedException(format("You have no rights"));
        
        comment.setCommentary(createCommentDto.getComment());
        comment.setTask(task);

        commentRepository.save(comment);
    }

    
}
