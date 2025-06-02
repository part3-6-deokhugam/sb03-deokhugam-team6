package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.PopularBook;
import com.part3.deokhugam.domain.enums.Period;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PopularBookRepository extends JpaRepository<PopularBook, UUID>, PopularBookRepositoryCustom {
    String period(Period period);
}
