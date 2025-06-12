package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID>, BookRepositoryCustom {

    boolean existsByIsbnAndDeletedFalse(String isbn);

    Optional<Book> findByIdAndDeletedFalse(UUID bookId);
}
