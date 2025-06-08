package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.UserRankData;
import com.part3.deokhugam.domain.enums.Period;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRankDataRepository extends JpaRepository<UserRankData, UUID>, UserRankDataRepositoryCustom {
}
