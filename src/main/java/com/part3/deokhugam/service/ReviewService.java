package com.part3.deokhugam.service;

import static java.time.ZoneOffset.UTC;

import com.part3.deokhugam.domain.Book;
import com.part3.deokhugam.domain.PopularReview;
import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.domain.ReviewLike;
import com.part3.deokhugam.domain.ReviewLikeId;
import com.part3.deokhugam.domain.ReviewMetrics;
import com.part3.deokhugam.domain.User;
import com.part3.deokhugam.domain.enums.Period;
import com.part3.deokhugam.dto.pagination.CursorPageResponsePopularReviewDto;
import com.part3.deokhugam.dto.pagination.CursorPageResponseReviewDto;
import com.part3.deokhugam.dto.review.PopularReviewDto;
import com.part3.deokhugam.dto.review.PopularReviewSearchCondition;
import com.part3.deokhugam.dto.review.ReviewCreateRequest;
import com.part3.deokhugam.dto.review.ReviewDto;
import com.part3.deokhugam.dto.review.ReviewLikeDto;
import com.part3.deokhugam.dto.review.ReviewSearchCondition;
import com.part3.deokhugam.dto.review.ReviewUpdateRequest;
import com.part3.deokhugam.exception.BusinessException;
import com.part3.deokhugam.exception.ErrorCode;
import com.part3.deokhugam.mapper.PopularReviewMapper;
import com.part3.deokhugam.mapper.ReviewLikeMapper;
import com.part3.deokhugam.mapper.ReviewMapper;
import com.part3.deokhugam.mapper.ReviewMetricsMapper;
import com.part3.deokhugam.repository.BookRepository;
import com.part3.deokhugam.repository.PopularReviewRepository;
import com.part3.deokhugam.repository.ReviewLikeRepository;
import com.part3.deokhugam.repository.ReviewMetricsRepository;
import com.part3.deokhugam.repository.ReviewRepository;
import com.part3.deokhugam.repository.ReviewRepositoryCustom;
import com.part3.deokhugam.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

  private static final double LIKE_WEIGHT = 0.3;
  private static final double COMMENT_WEIGHT = 0.7;

  private final ReviewRepository reviewRepository;
  private final ReviewRepositoryCustom reviewRepositoryCustom;
  private final UserRepository userRepository;
  private final BookRepository bookRepository;
  private final ReviewLikeRepository reviewLikeRepository;
  private final ReviewMetricsRepository reviewMetricsRepository;
  private final PopularReviewRepository popularReviewRepository;

  private final NotificationService notificationService;

  private final ReviewMapper reviewMapper;
  private final ReviewMetricsMapper reviewMetricsMapper;
  private final ReviewLikeMapper reviewLikeMapper;
  private final PopularReviewMapper popularReviewMapper;

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
    ReviewMetrics metrics = reviewMetricsMapper.toReviewMetrics(review);
    review.setMetrics(metrics);

    Review savedReview = reviewRepository.save(review);
    ReviewMetrics savedMetrics = reviewMetricsRepository.save(metrics);

    return reviewMapper.toDto(savedReview, savedMetrics);
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

    if (!reviewLike.isLiked()) {
      reviewLike.setLiked(true);

      String notifContent = user.getNickname() + "님이 좋아요를 눌렀습니다.";
      notificationService.createNotification(
          review.getUser().getId(),
          review,
          notifContent
      );
      reviewLikeRepository.save(reviewLike);

      review.getMetrics().setLikeCount(review.getMetrics().getLikeCount() + 1);
      return reviewLikeMapper.toReviewLikeDto(userId, reviewId, true);
    }

    reviewLike.setLiked(false);
    review.getMetrics().setLikeCount(review.getMetrics().getLikeCount() - 1);
    return reviewLikeMapper.toReviewLikeDto(userId, reviewId, false);
  }

  @Transactional
  public ReviewDto findById(UUID reviewId, UUID userId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Review ID: " + reviewId));
    ReviewMetrics reviewMetrics = reviewMetricsRepository.findById(reviewId)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND,
                "ReviewMetrics ID: " + reviewId));

    boolean likedByMe = isLikedByMe(reviewId, userId);

    return reviewMapper.toDto(review, reviewMetrics, likedByMe);
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
            () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND,
                "ReviewMetrics ID: " + reviewId));

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

  @Transactional(readOnly = true)
  public CursorPageResponsePopularReviewDto getPopularReviews(PopularReviewSearchCondition condition) {

    Integer cursorRank = condition.getCursor() != null ? Integer.parseInt(condition.getCursor()) : null;

    List<PopularReview> popularReviews = popularReviewRepository.findPopularReviewsWithCursor(
        condition.getPeriod(),
        String.valueOf(condition.getDirection()),
        cursorRank,
        condition.getAfter(),
        condition.getLimit() + 1
    );

    boolean hasNext = popularReviews.size() > condition.getLimit();
    if (hasNext) {
      popularReviews.remove(popularReviews.size() - 1);
    }

    List<PopularReviewDto> content = popularReviews.stream()
        .map(popularReviewMapper::toDto)
        .toList();

    String nextCursor = hasNext ? String.valueOf(popularReviews.get(popularReviews.size() - 1).getRank()) : null;
    Instant nextAfter = hasNext ? popularReviews.get(popularReviews.size() - 1).getCreatedAt() : null;

    int totalCount = popularReviewRepository.countByPeriodType(condition.getPeriod());

    return new CursorPageResponsePopularReviewDto(
        content,
        nextCursor,
        nextAfter,
        content.size(),
        totalCount,
        hasNext
    );
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

  public void calculateReview(Period period) {
    LocalDate periodDate = LocalDate.now();

    if (period == Period.ALL_TIME) {
      List<Review> reviews = reviewRepository.findByDeletedFalse();

      List<PopularReview> allTimePopularReviews = reviews.stream()
          .filter(review ->
              review.getMetrics() != null)
          .map(
              review -> {
                ReviewMetrics metrics = review.getMetrics();

                int likeCount = metrics.getLikeCount();
                int commentCount = metrics.getCommentCount();

                double score = likeCount * LIKE_WEIGHT + commentCount * COMMENT_WEIGHT;

                return popularReviewMapper.toPopularReview(review, period, periodDate, score,
                    likeCount, commentCount);
              }
          ).sorted(Comparator.comparing(PopularReview::getScore).reversed())
          .toList();
      for (int i = 0; i < allTimePopularReviews.size(); i++) {
        allTimePopularReviews.get(i).setRank(i + 1);
      }

      popularReviewRepository.saveAll(allTimePopularReviews);
      return;
    }

    Instant start;
    Instant end;

    switch (period) {
      case DAILY:
        start = periodDate.minusDays(1).atStartOfDay(UTC).toInstant();
        end = periodDate.minusDays(1).atTime(LocalTime.MAX).atZone(UTC).toInstant();
        break;
      case WEEKLY:
        start = periodDate.minusWeeks(1).atStartOfDay(UTC).toInstant();
        end = periodDate.minusDays(1).atTime(LocalTime.MAX).atZone(UTC).toInstant();
        break;
      case MONTHLY:
        start = periodDate.minusMonths(1).atStartOfDay(UTC).toInstant();
        end = periodDate.minusDays(1).atTime(LocalTime.MAX).atZone(UTC).toInstant();
        break;
      default:
        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }

    final Instant finalStart = start;
    final Instant finalEnd = end;

    List<Review> reviews = reviewRepository.findByDeletedFalse();

    List<PopularReview> periodPopularReviews = reviews.stream()
        .map(review -> {
              int likeCount = (int) review.getLikes().stream()
                  .filter(like ->
                      like.isLiked() &&
                          like.getCreatedAt() != null &&
                          !like.getCreatedAt().isBefore(finalStart) &&
                          !like.getCreatedAt().isAfter(finalEnd)
                  )
                  .count();

              int commentCount = (int) review.getComments().stream()
                  .filter(comment ->
                      !comment.isDeleted() &&
                          comment.getUpdatedAt() != null &&
                          !comment.getUpdatedAt().isBefore(finalStart) &&
                          !comment.getUpdatedAt().isAfter(finalEnd)
                  )
                  .count();

              double score = likeCount * LIKE_WEIGHT + commentCount * COMMENT_WEIGHT;
              return popularReviewMapper.toPopularReview(review, period, periodDate, score,
                  likeCount, commentCount);
            }
        ).sorted(Comparator.comparing(PopularReview::getScore).reversed())
        .toList();
    for (int i = 0; i < periodPopularReviews.size(); i++) {
      periodPopularReviews.get(i).setRank(i + 1);
    }

    popularReviewRepository.saveAll(periodPopularReviews);
  }
}
