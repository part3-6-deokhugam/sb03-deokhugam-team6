package com.part3.deokhugam.service;

import com.part3.deokhugam.domain.Notification;
import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.dto.notification.NotificationDto;
import com.part3.deokhugam.dto.notification.NotificationUpdateRequest;
import com.part3.deokhugam.dto.pagination.CursorPageResponseNotificationDto;
import com.part3.deokhugam.exception.ErrorCode;
import com.part3.deokhugam.exception.NotificationException;
import com.part3.deokhugam.mapper.NotificationMapper;
import com.part3.deokhugam.repository.NotificationRepository;
import java.util.Map;
import com.part3.deokhugam.repository.UserRepository;
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
  private final NotificationMapper mapper;
  private final UserRepository userRepository;

  @Transactional
  public NotificationDto createNotification(
      UUID targetUserId,
      Review review,
      String customMessage
  ) {
    String content = chooseContent(review, customMessage);

    boolean exists = repo.existsByUserIdAndReviewIdAndContent(
        targetUserId, review.getId(), content);
    if (exists) {
      return null;
    }

    Notification n = Notification.builder()
        .user(userRepository.getReferenceById(targetUserId))
        .review(review)
        .content(content)
        .build();
    Notification saved = repo.save(n);

    return mapper.toDto(saved);
  }

  private String chooseContent(Review review, String customMessage) {
    if (customMessage != null && !customMessage.isBlank()) {
      return customMessage;
    }
    return truncate(review.getContent(), 50);  // 최대 50자까지만
  }

  private String truncate(String text, int maxLen) {
    if (text.length() <= maxLen) {
      return text;
    }
    return text.substring(0, maxLen) + "...";
  }

  /**
   * 커서 페이지 방식으로 알림 목록 조회
   */
  @Transactional(readOnly = true)
  public CursorPageResponseNotificationDto findNotifications(
      UUID userId, String direction,
      String cursor, String after, int limit
  ) {
    boolean asc = "ASC".equalsIgnoreCase(direction);
    Instant cursorInstant = (cursor != null && !cursor.isBlank())
        ? parseOrBadRequest(cursor)
        : null;
    Instant afterInstant = (after != null && !after.isBlank())
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
      // 새 알림 조회(오름차순)
      raw = repo.findByUserIdAndCreatedAtAfterOrderByCreatedAtAsc(userId, afterInstant, pg);
    } else if (cursorInstant != null) {
      // 커서 기반 페이지네이션(내림차순)
      raw = repo.findByUserIdAndCreatedAtBeforeOrderByCreatedAtDesc(userId, cursorInstant, pg);
    } else {
      // 첫 페이지(내림차순)
      raw = repo.findByUserIdOrderByCreatedAtDesc(userId, pg);
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
    Instant nextAfter = !page.isEmpty()
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

  /**
   * 단일 알림 읽음 상태 업데이트
   */
  @Transactional
  public NotificationDto updateConfirmed(
      UUID notificationId, UUID userId, NotificationUpdateRequest req
  ) {
    Notification n = repo.findById(notificationId)
        .orElseThrow(() -> new NotificationException(ErrorCode.NOTIFICATION_NOT_FOUND,
            Map.of("notificationId", notificationId.toString())));

    if (!n.getUser().getId().equals(userId)) {
      throw new NotificationException(ErrorCode.NOTIFICATION_FORBIDDEN,
          Map.of(
              "notificationId", notificationId.toString(),
              "userId", userId.toString()
          ));
    }

    mapper.updateFromRequest(req, n);

    return mapper.toDto(n);
  }

  private Instant parseOrBadRequest(String iso) {
    try {
      return Instant.parse(iso);
    } catch (Exception e) {
      throw new NotificationException(ErrorCode.INVALID_INPUT_VALUE,
          Map.of("invalidParameter", iso));
    }
  }

  @Transactional
  public void markAllRead(UUID userId) {
    // 1) 아직 읽지 않은 알림만 조회
    List<Notification> unread = repo.findByUserIdAndConfirmedFalse(userId);

    // 2) 모두 confirmed=true 로 변경
    unread.forEach(n -> n.setConfirmed(true));

    // 3) 변경사항 저장
    repo.saveAll(unread);
  }

  @Transactional(readOnly = true)
  public boolean existsNotification(
      UUID userId,
      UUID reviewId,
      String content,
      Instant start,
      Instant end
  ) {
    return repo.existsByUserIdAndReviewIdAndContentAndCreatedAtBetween(
        userId, reviewId, content, start, end
    );
  }

  // 좋아요 삭제시 기존 알림 삭제
  @Transactional
  public void deleteLikeNotification(UUID targetUserId, UUID reviewId) {
    // 키워드로 알림구분
    repo.deleteLikeNotifications(targetUserId, reviewId, "좋아요");
  }
}