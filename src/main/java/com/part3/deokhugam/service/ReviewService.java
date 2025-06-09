package com.part3.deokhugam.service;

import com.part3.deokhugam.domain.Book;
import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.domain.ReviewLike;
import com.part3.deokhugam.domain.ReviewLikeId;
import com.part3.deokhugam.domain.ReviewMetrics;
import com.part3.deokhugam.domain.User;
import com.part3.deokhugam.dto.review.ReviewCreateRequest;
import com.part3.deokhugam.dto.review.ReviewDto;
import com.part3.deokhugam.dto.review.ReviewLikeDto;
import com.part3.deokhugam.dto.review.ReviewUpdateRequest;
import com.part3.deokhugam.exception.BusinessException;
import com.part3.deokhugam.exception.ErrorCode;
import com.part3.deokhugam.mapper.ReviewLikeMapper;
import com.part3.deokhugam.mapper.ReviewMapper;
import com.part3.deokhugam.mapper.ReviewMetricsMapper;
import com.part3.deokhugam.repository.BookRepository;
import com.part3.deokhugam.repository.ReviewLikeRepository;
import com.part3.deokhugam.repository.ReviewMetricsRepository;
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
  private final ReviewLikeRepository reviewLikeRepository;
  private final ReviewMetricsRepository reviewMetricsRepository;

  private final ReviewMapper reviewMapper;
  private final ReviewMetricsMapper reviewMetricsMapper;
  private final ReviewLikeMapper reviewLikeMapper;

  @Transactional
  public ReviewDto create(ReviewCreateRequest request) {
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND,
            "User ID: " + request.getUserId()));
    Book book = bookRepository.findById(request.getBookId())
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND,
            "Book ID: " + request.getBookId()));

    boolean exists = reviewRepository.existsByBookIdAndUserIdAndDeletedFalse(book.getId(),
        user.getId());
    if (exists) {
      throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE,
          "User ID: " + request.getUserId() + ", Book ID: " + request.getBookId());
    }

    Review review = reviewMapper.toReview(request, user, book);
    Review savedReview = reviewRepository.save(review);

    ReviewMetrics reviewMetrics = reviewMetricsMapper.toReviewMetrics(review);
    ReviewMetrics savedReviewMetrics = reviewMetricsRepository.save(reviewMetrics);

    return reviewMapper.toDto(savedReview, savedReviewMetrics);
  }

  @Transactional
  public ReviewLikeDto like(UUID reviewId, UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND,
            "User ID: " + userId));
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND,
            "review ID: " + reviewId));

    ReviewLike reviewLike = reviewLikeRepository.findByReview_IdAndUser_Id(reviewId, userId)
        .orElse(reviewLikeMapper.toReviewLike(user, review, false));

    if(!reviewLike.isLiked()){
      reviewLike.setLiked(true);
      return reviewLikeMapper.toReviewLikeDto(userId, reviewId, true);
    }

    reviewLike.setLiked(false);
    return reviewLikeMapper.toReviewLikeDto(userId, reviewId, false);
  }

  @Transactional
  public ReviewDto findById(UUID reviewId, UUID userId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Review ID: " + reviewId));
    User user = userRepository.findById(userId)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "User ID: " + userId));
    ReviewMetrics reviewMetrics = reviewMetricsRepository.findById(reviewId)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "ReviewMetrics ID: " + reviewId));

    boolean likedByMe = isLikedByMe(reviewId, userId);

    return reviewMapper.toDto(review, reviewMetrics, likedByMe);
  }

  @Transactional
  public ReviewDto create(ReviewCreateRequest request) {
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND,
            "User ID: " + request.getUserId()));
    Book book = bookRepository.findById(request.getBookId())
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND,
            "Book ID: " + request.getBookId()));

    boolean exists = reviewRepository.existsByBookIdAndUserIdAndDeletedFalse(book.getId(),
        user.getId());
    if (exists) {
      throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE,
          "User ID: " + request.getUserId() + ", Book ID: " + request.getBookId());
    }

    Review review = reviewMapper.toReview(request, user, book);
    Review savedReview = reviewRepository.save(review);

    return reviewMapper.toDto(savedReview);
  }

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
        .orElseThrow(
            () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "User ID: " + userId));
    ReviewMetrics reviewMetrics = reviewMetricsRepository.findById(reviewId)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "ReviewMetrics ID: " + reviewId));

    if (!review.getUser().getId().equals(userId)) {
      throw new BusinessException(ErrorCode.FORBIDDEN,
          "User ID: " + userId + ", Review ID: " + reviewId);
    }
    if (review.isDeleted()) {
      throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Review ID: " + reviewId);
    }

    boolean likedByMe = isLikedByMe(reviewId, userId);

    review.setContent(request.getContent());
    review.setRating(request.getRating());

    return reviewMapper.toDto(review, reviewMetrics, likedByMe);
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

  public boolean isLikedByMe(UUID reviewId, UUID userId) {
    return reviewLikeRepository.findById(new ReviewLikeId(reviewId, userId))
        .map(ReviewLike::isLiked)
        .orElse(false);
  }
}
