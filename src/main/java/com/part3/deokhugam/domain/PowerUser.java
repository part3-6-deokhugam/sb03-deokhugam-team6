package com.part3.deokhugam.domain;

import com.part3.deokhugam.domain.enums.Period;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "popular_user",
    indexes = {
        @Index(name = "idx_popular_user_period_type_period_date", columnList = "period_type, period_date"),
        @Index(name = "idx_popular_user_period_type_score_rank", columnList = "period_type, score, rank")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PowerUser extends BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  //FK: User 엔티티 (ON DELETE CASCADE)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT)  // DDL에서 ON DELETE CASCADE 옵션을 사용해야 함
  )
  private User user;

  //기간 타입 (DAILY, WEEKLY, MONTHLY, ALL_TIME 등)
  @Enumerated(EnumType.STRING)
  @Column(name = "period_type", nullable = false, length = 20)
  private Period periodType;

  //기간 기준 날짜 (예: daily → 해당 날짜, weekly → 주의 시작일, monthly → 해당 월의 첫째 날)
  @Column(name = "period_date", nullable = false)
  private LocalDate periodDate;

  //해당 기간 동안 리뷰 점수의 합계
  @Column(name = "review_score_sum", nullable = false, precision = 19, scale = 4)
  private BigDecimal reviewScoreSum;

  //활동 점수 → 평점 평균*0.6 + 리뷰 수*0.4 등의 방식으로 계산 (NUMERIC 타입)
  @Column(name = "score", nullable = false, precision = 19, scale = 4)
  private BigDecimal score;

  //순위 (INTEGER)
  @Column(name = "rank", nullable = false)
  private Integer rank;

  //좋아요 수 (INTEGER)
  @Column(name = "like_count", nullable = false)
  private Integer likeCount;

  //댓글 수 (INTEGER)
  @Column(name = "comment_count", nullable = false)
  private Integer commentCount;
}