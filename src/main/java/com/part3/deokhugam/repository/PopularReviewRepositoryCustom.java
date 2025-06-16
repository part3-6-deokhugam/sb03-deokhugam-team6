package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.PopularReview;
import com.part3.deokhugam.domain.enums.Period;
import java.time.Instant;
import java.util.List;

public interface PopularReviewRepositoryCustom {
  List<PopularReview> findPopularReviewsWithCursor(
      Period period,
      String direction,
      Integer cursorRank,
      Instant after,
      int limit
  );
}
