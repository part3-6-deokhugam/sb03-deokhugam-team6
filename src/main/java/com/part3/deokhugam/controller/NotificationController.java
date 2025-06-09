package com.part3.deokhugam.controller;

import com.part3.deokhugam.api.NotificationApi;
import com.part3.deokhugam.dto.notification.NotificationDto;
import com.part3.deokhugam.dto.notification.NotificationUpdateRequest;
import com.part3.deokhugam.dto.pagination.CursorPageResponseNotificationDto;
import com.part3.deokhugam.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {

  private final NotificationService notificationService;

  @Override
  public ResponseEntity<CursorPageResponseNotificationDto> getNotifications(
      UUID userId, String direction, String cursor, String after, int limit) {
    return ResponseEntity.ok(
        notificationService.findNotifications(userId, direction, cursor, after, limit)
    );
  }

  @Override
  public ResponseEntity<NotificationDto> patchNotification(
      UUID notificationId, UUID userId, NotificationUpdateRequest req) {
    return ResponseEntity.ok(
        notificationService.updateConfirmed(notificationId, userId, req)
    );
  }
}