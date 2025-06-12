package com.part3.deokhugam.dto.pagination;

import com.part3.deokhugam.dto.user.PowerUserDto;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CursorPageResponsePowerUserDto {
  private List<PowerUserDto> content;
  private String nextCursor;
  private Instant nextAfter;
  private int size;
  private long totalElements;
  private boolean hasNext;
}
