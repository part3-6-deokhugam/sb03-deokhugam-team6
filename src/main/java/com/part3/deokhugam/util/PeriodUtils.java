package com.part3.deokhugam.util;

import com.part3.deokhugam.domain.enums.Period;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class PeriodUtils {

  public static Instant calculateStart(Period period, ZoneId zone) {
    ZonedDateTime now = ZonedDateTime.now(zone);
    return switch (period) {
      case DAILY     -> now.truncatedTo(ChronoUnit.DAYS).toInstant();
      case WEEKLY    -> now.with(DayOfWeek.MONDAY)
          .truncatedTo(ChronoUnit.DAYS)
          .toInstant();
      case MONTHLY   -> now.withDayOfMonth(1)
          .truncatedTo(ChronoUnit.DAYS)
          .toInstant();
      case ALL_TIME  -> Instant.EPOCH;  // 전체 기간
    };
  }

  public static Instant calculateEnd(ZoneId zone) {
    return ZonedDateTime.now(zone).toInstant();
  }
}