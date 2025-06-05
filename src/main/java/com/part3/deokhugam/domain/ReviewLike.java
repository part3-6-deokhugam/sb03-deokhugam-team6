package com.part3.deokhugam.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "review_like")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLike {

  @EmbeddedId
  private ReviewLikeId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("reviewId")
  @JoinColumn(name = "review_id", nullable = false)
  private Review review;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private boolean liked = false;

  @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT now()")
  private ZonedDateTime createdAt = ZonedDateTime.now();
}
