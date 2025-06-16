package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.PowerUser;
import com.part3.deokhugam.domain.enums.Period;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PowerUserRepository extends JpaRepository<PowerUser, UUID>, PowerUserRepositoryCustom {
  void deleteByPeriodTypeAndPeriodDate(Period periodType, LocalDate periodDate);
}
