package com.part3.deokhugam.domain;

import com.part3.deokhugam.domain.enums.Period;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

  @Entity
  @Table(name = "popular_review",
      uniqueConstraints = @UniqueConstraint(
          columnNames = {"review_id", "period_type", "period_date"}))
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public class PopularReview extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", nullable = false, columnDefinition = "period_type")
    private Period periodType;

    @Column(name = "period_date", nullable = false)
    private LocalDate periodDate;

    @Column(nullable = false)
    private double score;

    @Column(nullable = false)
    private int rank;
  }
