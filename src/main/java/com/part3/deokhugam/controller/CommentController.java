package com.part3.deokhugam.controller;

import com.part3.deokhugam.api.CommentApi;
import com.part3.deokhugam.dto.comment.CommentCreateRequest;
import com.part3.deokhugam.dto.comment.CommentDto;
import com.part3.deokhugam.dto.comment.CommentUpdateRequest;

import com.part3.deokhugam.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


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

    @Override
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> update(
            @PathVariable UUID commentId,
            @RequestHeader("Deokhugam-Request-User-ID") UUID userId,
            @RequestBody @Valid CommentUpdateRequest request) {

        CommentDto updated = commentService.update(commentId, userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }
  
    @Override
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteLogically(
            @PathVariable UUID commentId,
            @RequestHeader("Deokhugam-Request-User-ID") UUID userId) {
        commentService.deleteLogically(commentId, userId);
        return ResponseEntity.noContent().build();
    }
  
    @Override
    @DeleteMapping("/{commentId}/hard")
    public ResponseEntity<Void> deletePhysically(
            @PathVariable UUID commentId,
            @RequestHeader("Deokhugam-Request-User-ID") UUID userId) {
        commentService.deletePhysically(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
