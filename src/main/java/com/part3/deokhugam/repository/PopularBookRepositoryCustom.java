package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.PopularBook;

import java.time.Instant;
import java.util.List;

public interface PopularBookRepositoryCustom {
    List<PopularBook> findListByCursor(String period, Instant after, String cursor, String direction, int limit);

    Long getTotalElements(String period);
}
