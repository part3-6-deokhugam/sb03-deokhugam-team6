package com.part3.deokhugam.controller;

import com.part3.deokhugam.dto.notification.NotificationDto;
import com.part3.deokhugam.dto.notification.NotificationUpdateRequest;
import com.part3.deokhugam.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
  private final NotificationService svc;

  // 목록 조회
  @GetMapping
  public ResponseEntity<List<NotificationDto>> getNotifications(
      @RequestHeader("Deokhugam-Request-User-ID") UUID userId) {
    return ResponseEntity.ok(svc.findAllForUser(userId));
  }

  // 단일 읽음 상태 변경
  @PatchMapping("/{notificationId}")
  public ResponseEntity<NotificationDto> patchNotification(
      @PathVariable UUID notificationId,
      @RequestBody NotificationUpdateRequest req) {
    return ResponseEntity.ok(svc.updateConfirmed(notificationId, req));
  }
}