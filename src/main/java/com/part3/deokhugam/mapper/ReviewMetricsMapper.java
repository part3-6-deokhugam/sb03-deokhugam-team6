package com.part3.deokhugam.mapper;

import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.domain.ReviewMetrics;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMetricsMapper {

  ReviewMetrics toReviewMetrics(Review review);
}
