package com.part3.deokhugam.batch;

import com.part3.deokhugam.domain.enums.Period;
import com.part3.deokhugam.domain.User;
import com.part3.deokhugam.domain.PowerUser;
import com.part3.deokhugam.repository.CommentRepository;
import com.part3.deokhugam.repository.ReviewRepository;
import com.part3.deokhugam.repository.PowerUserRepository;
import com.part3.deokhugam.repository.UserRepository;
import java.util.ArrayList;
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
  private final PowerUserRepository    powerUserRepository;

  // 매일 00:00에 실행 (Asia/Seoul)
  @Scheduled(cron = "${scheduler.batch.start-time}", zone = "Asia/Seoul")
  @Transactional
  public void calculateAndSaveAllPeriods() {
    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

    // DAILY, WEEKLY, MONTHLY, ALL_TIME 처리
    for (Period period : Period.values()) {
      LocalDate periodDate = calculatePeriodDate(period, today);
      powerUserRepository.deleteByPeriodTypeAndPeriodDate(period, periodDate);
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
        .stream().filter(u -> !u.isDeleted())
        .toList();

    // 2-3) 임시 리스트에 추가
    List<PowerUser> list = new ArrayList<>();
    for (User u : users) {
      double sum = reviewRepository.sumRatingByUserAndPeriod(u.getId(), start, end);
      long likeCount = reviewRepository.countLikesByUserAndPeriod(u.getId(), start, end);
      long commentCount = commentRepository.countByUserAndPeriod(u.getId(), start, end);

      BigDecimal score = BigDecimal.valueOf(sum)
          .multiply(BigDecimal.valueOf(0.5))
          .add(BigDecimal.valueOf(likeCount).multiply(BigDecimal.valueOf(0.2)))
          .add(BigDecimal.valueOf(commentCount).multiply(BigDecimal.valueOf(0.3)));

      list.add(PowerUser.builder()
          .user(u)
          .periodType(period)
          .periodDate(periodDate)
          .reviewScoreSum(BigDecimal.valueOf(sum))
          .likeCount((int) likeCount)
          .commentCount((int) commentCount)
          .score(score)
          .build());
    }

    // 3) 점수 내림차순 정렬
    list.sort((a, b) -> b.getScore().compareTo(a.getScore()));

    // 4) 순위 세팅
    for (int i = 0; i < list.size(); i++) {
      list.get(i).setRank(i + 1);
    }

    // 5) 한번에 저장
    powerUserRepository.saveAll(list);
  }
}
