package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.UserRankData;
import com.part3.deokhugam.domain.enums.Period;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface UserRankDataRepositoryCustom {
  List<UserRankData> findByPeriodWithCursor(
      Period period,
      LocalDate periodDate,
      String direction,
      String cursor,
      Instant after,
      int limit
  );
  long countByPeriod(Period period, LocalDate periodDate);
}