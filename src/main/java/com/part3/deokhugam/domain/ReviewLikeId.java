package com.part3.deokhugam.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLikeId implements Serializable {

  private UUID reviewId;
  private UUID userId;

  // 복합키 식별을 위한 오버라이드
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ReviewLikeId)) return false;
    ReviewLikeId that = (ReviewLikeId) o;
    return Objects.equals(reviewId, that.reviewId) &&
        Objects.equals(userId, that.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(reviewId, userId);
  }
}
