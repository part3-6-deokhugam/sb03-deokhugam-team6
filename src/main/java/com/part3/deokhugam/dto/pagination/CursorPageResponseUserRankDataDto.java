package com.part3.deokhugam.dto.pagination;

import com.part3.deokhugam.dto.user.UserRankDataDto;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CursorPageResponseUserRankDataDto {
  private List<UserRankDataDto> content;

  // 마지막 요소의 rank 값을 문자열로 넘기기
  private String nextCursor;

  // 마지막 요소의 createdAt
  private Instant nextAfter;

  private int size;           // 실제 리턴된 개수
  private long totalElements;
  private boolean hasNext;    // 다음 페이지 유무 여부
}
