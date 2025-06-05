package com.part3.deokhugam.controller;

import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.dto.review.ReviewCreateRequest;
import com.part3.deokhugam.dto.review.ReviewDto;
import com.part3.deokhugam.dto.review.ReviewLikeDto;
import com.part3.deokhugam.dto.review.ReviewUpdateRequest;
import com.part3.deokhugam.service.ReviewService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

  private final ReviewService reviewService;

  @PostMapping
  public ResponseEntity<ReviewDto> create(@RequestBody @Valid ReviewCreateRequest request) {
    ReviewDto reviewDto = reviewService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(reviewDto);
  }

  @PostMapping("/{reviewId}/like")
  public ResponseEntity<ReviewLikeDto> like(@PathVariable UUID reviewId,
      @RequestHeader(value = "Deokhugam-Request-User-ID", required = true) UUID userId ){
    ReviewLikeDto reviewLikeDto = reviewService.like(reviewId, userId);
    return ResponseEntity.status(HttpStatus.OK).body(reviewLikeDto);
  }

  @GetMapping("/{reviewId}")
  public ResponseEntity<ReviewDto> findById(@PathVariable UUID reviewId,
      @RequestHeader(value = "Deokhugam-Request-User-ID", required = true) UUID userId){
    ReviewDto reviewDto = reviewService.findById(reviewId, userId);
    return ResponseEntity.ok(reviewDto);
  }

  @PatchMapping("/{reviewId}")
  public ResponseEntity<ReviewDto> update(@PathVariable UUID reviewId,
      @RequestHeader(value = "Deokhugam-Request-User-ID", required = true) UUID userId,
      @RequestBody @Valid ReviewUpdateRequest request) {
    ReviewDto reviewDto = reviewService.update(reviewId, userId, request);
    return ResponseEntity.status(HttpStatus.OK).body(reviewDto);
  }

  @DeleteMapping("/{reviewId}")
  public ResponseEntity<Void> delete(
      @PathVariable UUID reviewId,
      @RequestHeader(value = "Deokhugam-Request-User-ID", required = true) UUID userId
  ) {
    reviewService.delete(reviewId, userId);
    return ResponseEntity.noContent().build(); // 204 No Content
  }

  @DeleteMapping("/{reviewId}/hard")
  public ResponseEntity<Void> hardDelete(
      @PathVariable UUID reviewId,
      @RequestHeader(value = "Deokhugam-Request-User-ID", required = true) UUID userId
  ) {
    reviewService.hardDelete(reviewId, userId);
    return ResponseEntity.noContent().build();
  }
}
