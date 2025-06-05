package com.part3.deokhugam.service;

import com.part3.deokhugam.domain.Book;
import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.domain.User;
import com.part3.deokhugam.dto.review.ReviewCreateRequest;
import com.part3.deokhugam.dto.review.ReviewDto;
import com.part3.deokhugam.dto.review.ReviewUpdateRequest;
import com.part3.deokhugam.exception.BusinessException;
import com.part3.deokhugam.exception.ErrorCode;
import com.part3.deokhugam.mapper.ReviewMapper;
import com.part3.deokhugam.repository.BookRepository;
import com.part3.deokhugam.repository.ReviewRepository;
import com.part3.deokhugam.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final UserRepository userRepository;
  private final BookRepository bookRepository;
  private final ReviewMapper reviewMapper;

  @Transactional
  public ReviewDto findById(UUID reviewId, UUID userId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Review ID: " + reviewId));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "User ID: " + userId));

    if (!review.getUser().getId().equals(userId)) {
      throw new BusinessException(ErrorCode.FORBIDDEN,
          "User ID: " + userId + ", Review ID: " + reviewId);
    }
    return reviewMapper.toDto(review);
  }

  @Transactional
  public ReviewDto update(UUID reviewId, UUID userId, ReviewUpdateRequest request) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Review ID: " + reviewId));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "User ID: " + userId));

    if (!review.getUser().getId().equals(userId)) {
      throw new BusinessException(ErrorCode.FORBIDDEN,
          "User ID: " + userId + ", Review ID: " + reviewId);
    }

    if (review.isDeleted()) {
      throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Review ID: " + reviewId);
    }

    review.setContent(request.getContent());
    review.setRating(request.getRating());

    return reviewMapper.toDto(review);
  }

  @Transactional
  public void delete(UUID reviewId, UUID userId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Review ID: " + reviewId));
    if (!review.getUser().getId().equals(userId)) {
      throw new BusinessException(ErrorCode.FORBIDDEN,
          "User ID: " + userId + ", Review ID: " + reviewId);
    }
    review.setDeleted(true);
  }

  @Transactional
  public void hardDelete(UUID reviewId, UUID userId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Review ID: " + reviewId));
    if (!review.getUser().getId().equals(userId)) {
      throw new BusinessException(ErrorCode.FORBIDDEN,
          "User ID: " + userId + ", Review ID: " + reviewId);
    }
    reviewRepository.delete(review);
  }
}
