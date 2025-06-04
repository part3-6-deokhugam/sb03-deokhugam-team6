package com.part3.deokhugam.dto.book;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record BookDto(
        UUID id,
        String title,
        String author,
        String description,
        String publisher,
        LocalDate publishedDate,
        String isbn,
        String thumbnailUrl,
        Integer reviewCount,
        BigDecimal rating,
        Instant createdAt,
        Instant updatedAt
) {

}
