package com.part3.deokhugam.dto.notification;

import lombok.Builder;
import lombok.Getter;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class NotificationDto {
  private UUID id;
  private UUID userId;
  private UUID reviewId;
  private String reviewTitle;    // DB엔 없으니 DTO만!
  private String content;
  private boolean confirmed;
  private Instant createdAt;
  private Instant updatedAt;
}