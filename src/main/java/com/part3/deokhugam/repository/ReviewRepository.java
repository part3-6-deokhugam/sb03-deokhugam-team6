package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.Review;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository  extends JpaRepository<Review, UUID> {
  boolean existsByBookIdAndUserIdAndDeletedFalse(UUID bookId, UUID userId);
  boolean existsByUserIdAndId(UUID userId, UUID reviewId);
}
