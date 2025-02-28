package com.example.task_manager.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.task_manager.dto.comment.CreateCommentDto;
import com.example.task_manager.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Comment Controller", description = "Comment Controller")
public class CommentController {

    private final CommentService commentService;

    @Operation(
        summary = "Leave a comment above task",
        description = "Leave a comment above task"
    )
    @PostMapping("/user/comment")
    public ResponseEntity<Void> createCommentEntity(@RequestBody CreateCommentDto createCommentDto,
                                                 @RequestHeader("Authorization") String authHeader) {
        
        commentService.createComment(createCommentDto, authHeader);

        return ResponseEntity.ok().build();
    }
    
}
