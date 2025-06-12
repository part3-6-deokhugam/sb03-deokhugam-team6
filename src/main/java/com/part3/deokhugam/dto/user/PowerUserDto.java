package com.part3.deokhugam.dto.user;

import com.part3.deokhugam.domain.PowerUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PowerUserDto {
  private String  userId;
  private String  nickname;
  private String  period;
  private String  createdAt;
  private long    rank;
  private double  score;
  private double  reviewScoreSum;
  private long    likeCount;
  private long    commentCount;

  public static PowerUserDto fromEntity(PowerUser e) {
    return new PowerUserDto(
        e.getUser().getId().toString(),
        e.getUser().getNickname(),
        e.getPeriodType().name(),
        e.getCreatedAt().toString(),
        e.getRank().longValue(),
        e.getScore().doubleValue(),
        e.getReviewScoreSum().doubleValue(),
        e.getLikeCount().longValue(),
        e.getCommentCount().longValue()
    );
  }
}
