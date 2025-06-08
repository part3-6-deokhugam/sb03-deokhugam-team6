package com.part3.deokhugam.mapper;

import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.domain.ReviewMetrics;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMetricsMapper {

  @Mapping(target = "reviewId", source = "review.id")
  @Mapping(target = "likeCount", ignore = true)
  @Mapping(target = "commentCount", ignore = true)
  ReviewMetrics toReviewMetrics(Review review);
}
