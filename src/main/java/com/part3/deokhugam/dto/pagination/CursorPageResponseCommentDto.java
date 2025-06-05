package com.part3.deokhugam.dto.pagination;

import com.part3.deokhugam.dto.comment.CommentDto;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class CursorPageResponseCommentDto {

  private List<CommentDto> content;
  private String nextCursor;
  private Instant nextAfter;
  private int size;
  private long totalElements;
  private boolean hasNext;
}
