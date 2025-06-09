package com.part3.deokhugam.service;

import com.part3.deokhugam.domain.Notification;
import com.part3.deokhugam.dto.notification.NotificationDto;
import com.part3.deokhugam.dto.notification.NotificationUpdateRequest;
import com.part3.deokhugam.dto.pagination.CursorPageResponseNotificationDto;
import com.part3.deokhugam.exception.BusinessException;
import com.part3.deokhugam.exception.ErrorCode;
import com.part3.deokhugam.mapper.NotificationMapper;
import com.part3.deokhugam.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository repo;
  private final NotificationMapper    mapper;

  /** 커서 페이지 방식으로 알림 목록 조회 */
  @Transactional(readOnly = true)
  public CursorPageResponseNotificationDto findNotifications(
      UUID userId, String direction,
      String cursor, String after, int limit
  ) {
    boolean asc = "ASC".equalsIgnoreCase(direction);
    Instant cursorInstant = (cursor != null && !cursor.isBlank())
        ? parseOrBadRequest(cursor)
        : null;
    Instant afterInstant  = (after  != null && !after.isBlank())
        ? parseOrBadRequest(after)
        : null;

    int pageSize = Math.max(1, limit);
    Pageable pg = PageRequest.of(
        0,
        pageSize + 1,
        asc ? Sort.by("createdAt").ascending()
            : Sort.by("createdAt").descending()
    );

    List<Notification> raw;
    if (afterInstant != null) {
      // 새 알림 확인용 (오름차순)
      raw = repo.findByUserIdAndCreatedAtAfter(userId, afterInstant, pg);
    } else {
      // 기본/과거 페이지네이션
      raw = repo.findByUserIdAndCreatedAtBefore(userId, cursorInstant, pg);
    }

    boolean hasNext = raw.size() > pageSize;
    List<Notification> page = hasNext
        ? raw.subList(0, pageSize)
        : raw;

    List<NotificationDto> dtos = page.stream()
        .map(mapper::toDto)
        .collect(Collectors.toList());

    Instant nextCursor = hasNext
        ? page.get(pageSize - 1).getCreatedAt()
        : null;
    Instant nextAfter  = !page.isEmpty()
        ? page.get(0).getCreatedAt()
        : null;

    long total = repo.countByUserId(userId);

    return CursorPageResponseNotificationDto.builder()
        .content(dtos)
        .hasNext(hasNext)
        .nextCursor(nextCursor)
        .nextAfter(nextAfter)
        .size(pageSize)
        .totalElements(total)
        .build();
  }

  /** 단일 알림 읽음 상태 업데이트 */
  @Transactional
  public NotificationDto updateConfirmed(
      UUID notificationId, UUID userId, NotificationUpdateRequest req
  ) {
    Notification n = repo.findById(notificationId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

    if (!n.getUser().getId().equals(userId)) {
      throw new BusinessException(ErrorCode.NO_NOTIFICATION_PERMISSION);
    }

    n.setConfirmed(req.isConfirmed());
    return mapper.toDto(n);
  }

  private Instant parseOrBadRequest(String iso) {
    try {
      return Instant.parse(iso);
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }
  }
}