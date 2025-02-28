package com.example.task_manager.service;

import com.example.task_manager.dto.comment.CreateCommentDto;

public interface CommentService {

    public void createComment(CreateCommentDto createCommentDto, String authHeader);
}
