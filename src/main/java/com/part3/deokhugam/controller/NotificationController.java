package com.part3.deokhugam.controller;

import com.part3.deokhugam.api.NotificationApi;
import com.part3.deokhugam.dto.notification.NotificationDto;
import com.part3.deokhugam.dto.notification.NotificationUpdateRequest;
import com.part3.deokhugam.dto.pagination.CursorPageResponseNotificationDto;
import com.part3.deokhugam.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {
  private final NotificationService notificationService;

  @Override
  @GetMapping
  public ResponseEntity<CursorPageResponseNotificationDto> getNotifications(
      @RequestParam("userId") UUID userId,
      String direction, String cursor, String after, int limit
  ) {
    var dto = notificationService.findNotifications(userId, direction, cursor, after, limit);
    return ResponseEntity.ok(dto);
  }

  @Override
  @GetMapping("/{notificationId}")
  public ResponseEntity<NotificationDto> patchNotification(
      @PathVariable UUID notificationId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID userId,
      @Valid @RequestBody NotificationUpdateRequest req
  ) {
    var updated = notificationService.updateConfirmed(notificationId, userId, req);
    return ResponseEntity.ok(updated);
  }

  @Override
  @PatchMapping("/read-all")
  public ResponseEntity<Void> patchReadAll(
      @RequestHeader("Deokhugam-Request-User-ID") UUID userId
  ) {
    notificationService.markAllRead(userId);
    return ResponseEntity.noContent().build(); // 204
  }
}