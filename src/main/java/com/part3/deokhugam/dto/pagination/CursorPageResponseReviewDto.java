package com.part3.deokhugam.dto.pagination;

import com.part3.deokhugam.dto.review.ReviewDto;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CursorPageResponseReviewDto {
  private List<ReviewDto> content;
  private String nextCursor;
  private Instant nextAfter;
  private int size;
  private int totalElements;
  private boolean hasNext;
}
