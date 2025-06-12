package com.part3.deokhugam.dto.review;

import com.part3.deokhugam.domain.enums.Period;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PopularReviewDto {
  private UUID id;
  private UUID reviewId;
  private UUID bookId;
  private String bookTitle;
  private String bookThumbnailUrl;
  private UUID userId;
  private String userNickname;
  private String reviewContent;
  private Double reviewRating;
  private Period period;
  private Instant createdAt;
  private Integer rank;
  private BigDecimal score;
  private Integer likeCount;
  private Integer commentCount;

}
