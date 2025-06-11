package com.part3.deokhugam.batch;

import com.part3.deokhugam.domain.enums.Period;
import com.part3.deokhugam.domain.User;
import com.part3.deokhugam.domain.UserRankData;
import com.part3.deokhugam.repository.CommentRepository;
import com.part3.deokhugam.repository.ReviewRepository;
import com.part3.deokhugam.repository.UserRankDataRepository;
import com.part3.deokhugam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserActivityScoreScheduler {

  private final UserRepository            userRepository;
  private final ReviewRepository          reviewRepository;
  private final CommentRepository         commentRepository;
  private final UserRankDataRepository    userRankDataRepository;

  // 매일 03:00에 실행 (Asia/Seoul)
  @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
  @Transactional
  public void calculateAndSaveAllPeriods() {
    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

    // DAILY, WEEKLY, MONTHLY, ALL_TIME 처리
    for (Period period : Period.values()) {
      LocalDate periodDate = calculatePeriodDate(period, today);
      calculatePeriod(period, periodDate);
    }
  }

  //기간별 기준 날짜 계산
  private LocalDate calculatePeriodDate(Period period, LocalDate today) {
    return switch (period) {
      case DAILY    -> today;
      case WEEKLY   -> today.with(DayOfWeek.MONDAY);
      case MONTHLY  -> today.withDayOfMonth(1);
      case ALL_TIME -> LocalDate.of(1970,1,1);
    };
  }

  //한 기간에 대해 사용자별 점수 계산·저장
  private void calculatePeriod(Period period, LocalDate periodDate) {
    // 2-1) 해당 기간 시작·끝 Instant
    ZoneId zone = ZoneId.of("Asia/Seoul");
    Instant start = periodDate.atStartOfDay(zone).toInstant();
    Instant end   = Instant.now();

    // 2-2) 모든 활성 유저 조회
    List<User> users = userRepository.findAll()
        .stream().filter(u -> !u.isDeleted()).toList();

    int rank = 1;
    for (User u : users) {
      UUID userId = u.getId();

      // 2-3) 리뷰 점수 합계 (ReviewMetrics 저장된 rating값 예시)
      double sum = reviewRepository.sumRatingByUserAndPeriod(userId, start, end);
      BigDecimal reviewScoreSum = BigDecimal.valueOf(sum);

      // 2-4) 좋아요 수
      long likeCount = reviewRepository.countLikesByUserAndPeriod(userId, start, end);

      // 2-5) 댓글 수
      long commentCount = commentRepository.countByUserAndPeriod(userId, start, end);

      // 2-6) 활동 점수 계산: 0.5*리뷰점수 + 0.2*좋아요 + 0.3*댓글
      BigDecimal score = reviewScoreSum
          .multiply(BigDecimal.valueOf(0.5))
          .add(BigDecimal.valueOf(likeCount).multiply(BigDecimal.valueOf(0.2)))
          .add(BigDecimal.valueOf(commentCount).multiply(BigDecimal.valueOf(0.3)));

      // 2-7) UserRankData 엔티티 빌드 & 저장
      UserRankData urd = UserRankData.builder()
          .user(u)
          .periodType(period)
          .periodDate(periodDate)
          .reviewScoreSum(reviewScoreSum)
          .likeCount((int) likeCount)
          .commentCount((int) commentCount)
          .score(score)
          .rank(rank++)
          .build();
      userRankDataRepository.save(urd);
    }
  }
}
