package com.part3.deokhugam.batch;

import com.part3.deokhugam.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor
public class NotificationCleanupScheduler {

  private final NotificationRepository notificationRepository;

  @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul")
  public void deleteOldConfirmedNotifications() {
    Instant cutoff = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        .minusDays(7)
        .toInstant();

    notificationRepository.deleteByConfirmedTrueAndUpdatedAtBefore(cutoff);
  }
}
