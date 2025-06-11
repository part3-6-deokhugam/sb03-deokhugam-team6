package com.part3.deokhugam.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import java.time.ZonedDateTime;
import org.springframework.data.annotation.CreatedDate;

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
  private boolean liked;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  public UUID getReviewId() {
    return id != null ? id.getReviewId() : null;
  }
}
