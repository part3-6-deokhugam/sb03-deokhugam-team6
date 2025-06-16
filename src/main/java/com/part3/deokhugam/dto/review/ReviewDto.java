package com.part3.deokhugam.dto.review;

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
public class ReviewDto {
  private UUID id;
  private UUID bookId;
  private String bookTitle;
  private String bookThumbnailUrl;
  private UUID userId;
  private String userNickname;
  private String content;
  private Double rating;
  private Integer likeCount;
  private Integer commentCount;
  @Builder.Default
  private Boolean likedByMe = false;
  private Instant createdAt;
  private Instant updatedAt;

}
