package com.part3.deokhugam.service;

import com.part3.deokhugam.domain.Book;
import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.domain.ReviewLike;
import com.part3.deokhugam.domain.ReviewLikeId;
import com.part3.deokhugam.domain.ReviewMetrics;
import com.part3.deokhugam.domain.User;
import com.part3.deokhugam.dto.pagination.CursorPageResponseReviewDto;
import com.part3.deokhugam.dto.review.ReviewCreateRequest;
import com.part3.deokhugam.dto.review.ReviewDto;
import com.part3.deokhugam.dto.review.ReviewLikeDto;
import com.part3.deokhugam.dto.review.ReviewSearchCondition;
import com.part3.deokhugam.dto.review.ReviewUpdateRequest;
import com.part3.deokhugam.exception.BookException;
import com.part3.deokhugam.exception.ErrorCode;
import com.part3.deokhugam.exception.ReviewException;
import com.part3.deokhugam.exception.UserException;
import com.part3.deokhugam.mapper.ReviewLikeMapper;
import com.part3.deokhugam.mapper.ReviewMapper;
import com.part3.deokhugam.mapper.ReviewMetricsMapper;
import com.part3.deokhugam.repository.BookRepository;
import com.part3.deokhugam.repository.ReviewLikeRepository;
import com.part3.deokhugam.repository.ReviewMetricsRepository;
import com.part3.deokhugam.repository.ReviewRepository;
import com.part3.deokhugam.repository.ReviewRepositoryCustom;
import com.part3.deokhugam.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final ReviewRepositoryCustom reviewRepositoryCustom;
  private final UserRepository userRepository;
  private final BookRepository bookRepository;
  private final ReviewLikeRepository reviewLikeRepository;
  private final ReviewMetricsRepository reviewMetricsRepository;

  private final ReviewMapper reviewMapper;
  private final ReviewMetricsMapper reviewMetricsMapper;
  private final ReviewLikeMapper reviewLikeMapper;

  @Transactional(readOnly = true)
  public CursorPageResponseReviewDto findAll(ReviewSearchCondition condition,
      UUID requestUserHeaderId) {
    List<Review> reviews = reviewRepositoryCustom.findAll(condition);

    boolean hasNext = reviews.size() > condition.getLimit();
    List<Review> currentPage = hasNext ? reviews.subList(0, condition.getLimit()) : reviews;

    List<ReviewDto> reviewDtoList = currentPage.stream()
        .map(review -> {
          ReviewMetrics reviewMetrics = reviewMetricsRepository.findById(review.getId())
              .orElse(null);
          boolean likedByMe = isLikedByMe(review.getId(), requestUserHeaderId);
          return reviewMapper.toDto(review, reviewMetrics, likedByMe);
        })
        .toList();

    String nextCursor = null;
    Instant nextAfter = null;

    if (hasNext) {
      Review last = currentPage.get(currentPage.size() - 1);
      nextAfter = last.getCreatedAt();

      if (condition.getOrderBy().equals("createdAt")) {
        nextCursor = nextAfter.toString();
      } else {
        double lastRating = last.getRating();
        nextCursor = Double.toString(lastRating);
      }
    }

    long totalElements = reviewRepositoryCustom.countByCondition(condition);

    return new CursorPageResponseReviewDto(
        reviewDtoList,
        nextCursor,
        nextAfter,
        condition.getLimit(),
        (int) totalElements,
        hasNext
    );
  }

  @Transactional
  public ReviewDto create(ReviewCreateRequest request) {
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new ReviewException(ErrorCode.USER_NOT_FOUND,
            Map.of("userId", request.getUserId().toString())));
    Book book = bookRepository.findById(request.getBookId())
        .orElseThrow(() -> new BookException(ErrorCode.BOOK_NOT_FOUND,
            Map.of("bookId", request.getBookId().toString())));

    boolean exists = reviewRepository.existsByBookIdAndUserIdAndDeletedFalse(book.getId(),
        user.getId());
    if (exists) {
      throw new ReviewException(ErrorCode.REVIEW_ALREADY_EXISTS,
          Map.of("userId", request.getUserId().toString(), "bookId",
              request.getBookId().toString()));
    }

    Review review = reviewMapper.toReview(request, user, book);
    ReviewMetrics metrics = reviewMetricsMapper.toReviewMetrics(review);
    review.setMetrics(metrics);

    Review savedReview = reviewRepository.save(review); // metrics도 같이 저장됨

    ReviewMetrics savedMetrics = reviewMetricsRepository.save(metrics);

    return reviewMapper.toDto(savedReview, savedMetrics);
  }

  @Transactional
  public ReviewLikeDto like(UUID reviewId, UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND,
            Map.of("userId", userId.toString())));
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND,
            Map.of("reviewId", reviewId.toString())));

    ReviewLike reviewLike = reviewLikeRepository.findByReview_IdAndUser_Id(reviewId, userId)
        .orElse(reviewLikeMapper.toReviewLike(user, review, false));

    if (!reviewLike.isLiked()) {
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
            () -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND,
                Map.of("reviewId", reviewId.toString())));
    ReviewMetrics reviewMetrics = reviewMetricsRepository.findById(reviewId)
        .orElseThrow(
            () -> new ReviewException(ErrorCode.REVIEW_METRICS_NOT_FOUND,
                Map.of("reviewMetricsId", reviewId.toString())));

    boolean likedByMe = isLikedByMe(reviewId, userId);

    return reviewMapper.toDto(review, reviewMetrics, likedByMe);
  }

  @Transactional
  public ReviewDto update(UUID reviewId, UUID userId, ReviewUpdateRequest request) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(
            () -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND,
                Map.of("reviewId", reviewId.toString())));
    User user = userRepository.findById(userId)
        .orElseThrow(
            () -> new UserException(ErrorCode.USER_NOT_FOUND, Map.of("userId", userId.toString())));
    ReviewMetrics reviewMetrics = reviewMetricsRepository.findById(reviewId)
        .orElseThrow(
            () -> new ReviewException(ErrorCode.REVIEW_METRICS_NOT_FOUND,
                Map.of("reviewMetricsId", reviewId.toString())));

    if (!review.getUser().getId().equals(userId)) {
      throw new ReviewException(ErrorCode.REVIEW_FORBIDDEN,
          Map.of("userId", userId.toString(), "reviewId", reviewId.toString()));
    }
    if (review.isDeleted()) {
      throw new ReviewException(ErrorCode.REVIEW_NOT_FOUND,
          Map.of("reviewId", reviewId.toString()));
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
            () -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND,
                Map.of("reviewId", reviewId.toString())));
    if (!review.getUser().getId().equals(userId)) {
      throw new ReviewException(ErrorCode.REVIEW_FORBIDDEN,
          Map.of("userId", userId.toString(), "reviewId", reviewId.toString()));
    }
    review.setDeleted(true);
  }

  @Transactional
  public void hardDelete(UUID reviewId, UUID userId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(
            () -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND,
                Map.of("reviewId", reviewId.toString())));
    if (!review.getUser().getId().equals(userId)) {
      throw new ReviewException(ErrorCode.REVIEW_FORBIDDEN,
          Map.of("userId", userId.toString(), "reviewId", reviewId.toString()));
    }
    reviewRepository.delete(review);
  }

  public boolean isLikedByMe(UUID reviewId, UUID userId) {
    return reviewLikeRepository.findById(new ReviewLikeId(reviewId, userId))
        .map(ReviewLike::isLiked)
        .orElse(false);
  }
}
