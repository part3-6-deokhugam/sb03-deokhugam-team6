package com.part3.deokhugam.dto.pagination;

import com.part3.deokhugam.dto.review.PopularReviewDto;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CursorPageResponsePopularReviewDto {
  private List<PopularReviewDto> content;
  private String nextCursor;
  private Instant nextAfter;
  private int size;
  private int totalElements;
  private boolean hasNext;
}
