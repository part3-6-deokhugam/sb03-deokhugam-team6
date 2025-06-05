package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.Review;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepositoryCustom {
    List<Review> findByCreatedAtBetween(Instant start, Instant end);

    List<Review> findByCreatedAtBetweenAndDeletedFalse(Instant start, Instant end);

    boolean existsByBookIdAndUserIdAndDeletedFalse(UUID bookId, UUID userId);
}
