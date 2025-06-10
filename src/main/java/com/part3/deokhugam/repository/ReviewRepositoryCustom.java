package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.dto.review.ReviewSearchCondition;
import java.util.List;

public interface ReviewRepositoryCustom {
  List<Review> findAll(ReviewSearchCondition condition);

  long countByCondition(ReviewSearchCondition condition);
}
