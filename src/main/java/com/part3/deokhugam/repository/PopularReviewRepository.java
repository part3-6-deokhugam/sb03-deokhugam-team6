package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.PopularReview;
import com.part3.deokhugam.domain.enums.Period;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopularReviewRepository extends JpaRepository<PopularReview, UUID>, PopularReviewRepositoryCustom {
  int countByPeriodType(Period period);
}
