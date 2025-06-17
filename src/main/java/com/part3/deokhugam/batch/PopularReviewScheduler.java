package com.part3.deokhugam.batch;

import com.part3.deokhugam.domain.enums.Period;
import com.part3.deokhugam.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PopularReviewScheduler {
  private final ReviewService reviewService;

  @Scheduled(cron = "0 10 14 * * *", zone = "Asia/Seoul")
  public void updatePopularReviews() {
    for(Period period : Period.values()) {
      reviewService.calculateReview(period);
    }
  }
}
