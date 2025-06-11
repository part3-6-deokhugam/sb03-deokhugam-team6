package com.part3.deokhugam.controller;

import com.part3.deokhugam.api.CommentApi;
import com.part3.deokhugam.dto.comment.CommentCreateRequest;
import com.part3.deokhugam.dto.comment.CommentDto;
import com.part3.deokhugam.dto.comment.CommentUpdateRequest;

import com.part3.deokhugam.dto.pagination.CursorPageResponseCommentDto;
import com.part3.deokhugam.exception.CommentException;
import com.part3.deokhugam.exception.ErrorCode;
import com.part3.deokhugam.service.CommentService;
import jakarta.validation.Valid;
import java.time.Instant;
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
  @GetMapping("/{commentId}")
  public ResponseEntity<CommentDto> findById(@PathVariable UUID commentId) {
    CommentDto commentDto = commentService.findById(commentId);
    return ResponseEntity.status(HttpStatus.OK).body(commentDto);
  }

  @Override
  @GetMapping
  public ResponseEntity<CursorPageResponseCommentDto> findAllByReviewId(
      @RequestParam("reviewId") UUID reviewId,
      @RequestParam(value = "direction", defaultValue = "DESC") String direction,
      @RequestParam(value = "cursor", required = false) String cursor,
      @RequestParam(value = "after", required = false) Instant after,
      @RequestParam(value = "limit", defaultValue = "50") int limit) {

    if (!direction.equalsIgnoreCase("ASC") && !direction.equalsIgnoreCase("DESC")) {
      throw new CommentException(ErrorCode.INVALID_INPUT_VALUE, "지원하지 않는 정렬 방향입니다.");
    }

    CursorPageResponseCommentDto response = commentService.findAll(reviewId, direction.toUpperCase(), cursor,
        after, limit);
    return ResponseEntity.status(HttpStatus.OK).body(response);
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
