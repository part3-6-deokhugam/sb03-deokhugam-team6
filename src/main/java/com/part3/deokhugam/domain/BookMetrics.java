package com.part3.deokhugam.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "book_metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookMetrics {

  @Id
  @Column(name = "book_id", nullable = false)
  private UUID id;

  @OneToOne
  @MapsId
  @JoinColumn(name = "book_id")
  private Book book;

  @Column(name = "review_count", nullable = false)
  private Integer reviewCount = 0;

  @Column(name = "average_rating", nullable = false, precision = 2, scale = 1)
  private BigDecimal averageRating = new BigDecimal("0.0");
}