package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.PowerUser;
import com.part3.deokhugam.domain.enums.Period;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface PowerUserRepositoryCustom {
  List<PowerUser> findByPeriodWithCursor(
      Period period,
      LocalDate periodDate,
      String direction,
      String cursor,
      Instant after,
      int limit
  );
  long countByPeriod(Period period, LocalDate periodDate);
}