package com.part3.deokhugam.mapper;

import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.domain.ReviewMetrics;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMetricsMapper {

  default ReviewMetrics toReviewMetrics(Review review) {
    return ReviewMetrics.builder()
        .reviewId(review.getId())
        .review(review)
        .likeCount(0)
        .commentCount(0)
        .build(); // 수정예정
  }
}
