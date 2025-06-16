package com.part3.deokhugam.batch;

import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.domain.enums.Period;
import com.part3.deokhugam.repository.ReviewMetricsRepository;
import com.part3.deokhugam.service.NotificationService;
import com.part3.deokhugam.util.PeriodUtils;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingNotificationScheduler {

  private final ReviewMetricsRepository metricsRepo;
  private final NotificationService notificationService;

  // 매주 알림 (Period.WEEKLY 쓰기)
  @Scheduled(cron = "0 0 9 * * MON", zone = "Asia/Seoul")
  public void weeklyTop10() {
    Instant start = PeriodUtils.calculateStart(Period.WEEKLY, ZoneId.of("Asia/Seoul"));
    Instant end   = PeriodUtils.calculateEnd(ZoneId.of("Asia/Seoul"));
    sendTop10Notifications(Period.WEEKLY, start, end);
  }

  // 매월 알림
  @Scheduled(cron = "0 0 9 1 * *", zone = "Asia/Seoul")
  public void monthlyTop10() {
    Instant start = PeriodUtils.calculateStart(Period.MONTHLY, ZoneId.of("Asia/Seoul"));
    Instant end   = PeriodUtils.calculateEnd(ZoneId.of("Asia/Seoul"));
    sendTop10Notifications(Period.MONTHLY, start, end);
  }

  // 실제 알림 생성 로직 분리
  private void sendTop10Notifications(Period period, Instant start, Instant end) {
    List<Review> top10 = metricsRepo.findTop10ReviewsByLikesInPeriod(start, end, PageRequest.of(0, 10));
    String tpl = switch (period) {
      case DAILY   -> "오늘 인기 10위 리뷰에 들었어요🎉";
      case WEEKLY  -> "이번 주 인기 10위 리뷰에 들었어요🎉";
      case MONTHLY -> "이번 달 인기 10위 리뷰에 들었어요🎉";
      default     -> "인기 10위 리뷰에 들었어요🎉";
    };

    for (Review r : top10) {
      UUID uid = r.getUser().getId();
      if (!notificationService.existsNotification(uid, r.getId(), tpl, start, end)) {
        notificationService.createNotification(uid, r, tpl);
      }
    }
  }
}