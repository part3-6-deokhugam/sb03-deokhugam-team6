package com.part3.deokhugam.dto.user;

import com.part3.deokhugam.domain.enums.Period;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRankDataDto {
  private UUID userId;
  private String nickname;
  private Period period;
  private Instant createdAt;
  private Integer rank;
  private BigDecimal score;
  private BigDecimal reviewScoreSum;
  private LocalDate periodDate;
  private Integer likeCount;
  private Integer commentCount;
}
