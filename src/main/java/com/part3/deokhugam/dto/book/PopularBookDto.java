package com.part3.deokhugam.dto.book;

import com.part3.deokhugam.domain.enums.Period;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PopularBookDto(
        UUID id,
        UUID bookId,
        String title,
        String author,
        String thumbnailUrl,
        Period period,
        Integer rank,
        Double score,
        Integer reviewCount,
        BigDecimal rating,
        Instant createdAt
) {
}