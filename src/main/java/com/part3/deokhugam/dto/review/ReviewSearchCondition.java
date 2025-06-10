package com.part3.deokhugam.dto.review;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewSearchCondition {
  private UUID userId;
  private UUID bookId;
  private String keyword;
  private String orderBy = "createdAt";
  private String direction = "DESC";
  private String cursor;
  private Instant after;
  private int limit = 50;
  private UUID requestUserId;
}
