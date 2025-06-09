package com.part3.deokhugam.dto.pagination;

import com.part3.deokhugam.dto.notification.NotificationDto;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class CursorPageResponseNotificationDto {
  private List<NotificationDto> content;    // 알림들
  private boolean hasNext;                  // 다음 페이지 존재 여부
  private Instant nextCursor;               // 다음 페이지 조회용 커서(createdAt)
  private Instant nextAfter;                // 새 알림 확인용 보조 커서(createdAt)
  private int size;                         // 요청한 페이지 크기
  private long totalElements;               // 전체 알림 개수
}