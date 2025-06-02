package com.part3.deokhugam.controller;

import com.part3.deokhugam.api.CommentApi;
import com.part3.deokhugam.dto.comment.CommentCreateRequest;
import com.part3.deokhugam.dto.comment.CommentDto;
import com.part3.deokhugam.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController implements CommentApi {
    private final CommentService commentService;

    @Override
    @PostMapping
    public ResponseEntity<CommentDto> create(@RequestBody @Valid CommentCreateRequest request) {
        CommentDto commentDto = commentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentDto);
    }
}
