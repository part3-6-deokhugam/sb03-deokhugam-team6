package com.part3.deokhugam.service;

import com.part3.deokhugam.domain.UserRankData;
import com.part3.deokhugam.domain.enums.Period;
import com.part3.deokhugam.dto.user.UserRankDataDto;
import com.part3.deokhugam.repository.UserRankDataRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRankDataService {

  private final UserRankDataRepository userRankDataRepository;

  /**
   * 특정 기간(period)에 해당하는 날짜(periodDate) 랭킹을 조회해서 DTO 목록으로 반환
   */
  public List<UserRankDataDto> getUserRanks(Period period, LocalDate periodDate) {
    List<UserRankData> entities =
        userRankDataRepository.findByPeriodAndPeriodDateOrderByRankAsc(period, periodDate);

    // 2) Entity → DTO 로 변환
    List<UserRankDataDto> dtos = entities.stream()
        .map(entity -> new UserRankDataDto(
            entity.getUser().getId(),
            entity.getUser().getNickname(),
            entity.getPeriodType(),
            entity.getCreatedAt(),          // 생성 시각 (BaseEntity 에서 상속받음)
            entity.getRank(),
            entity.getScore(),
            entity.getReviewScoreSum(),
            entity.getPeriodDate(),
            entity.getLikeCount(),
            entity.getCommentCount()
        ))
        .collect(Collectors.toList());

    return dtos;
  }

  public List<UserRankDataDto> getUserRanksForToday(Period period) {
    LocalDate today = LocalDate.now();
    return getUserRanks(period, today);
  }
}
