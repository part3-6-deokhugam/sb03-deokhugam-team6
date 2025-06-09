package com.part3.deokhugam.dto.pagination;

import com.part3.deokhugam.dto.notification.NotificationDto;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class CursorPageResponseNotificationDto {
  private List<NotificationDto> data;
  private boolean hasNext;
  private Instant nextCursor;   // 다음 페이지를 불러올 커서(=마지막 항목의 createdAt)
}