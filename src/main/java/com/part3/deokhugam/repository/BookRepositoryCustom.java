package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.Book;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepositoryCustom {
    Optional<Book> findByIdNotDelete(UUID id);

    List<Book> findListByCursor(String keyword, Instant after, String cursor, String orderBy, String direction, int limit);

    Long getTotalElements(String keyword);
}
