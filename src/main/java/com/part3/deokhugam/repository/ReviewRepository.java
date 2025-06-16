package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.Review;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepositoryCustom {
    List<Review> findByCreatedAtBetweenAndDeletedFalse(Instant start, Instant end);

    boolean existsByBookIdAndUserIdAndDeletedFalse(UUID bookId, UUID userId);

    List<Review> findByDeletedFalse();

    List<Review> findByBookIdAndDeletedFalse(UUID bookId);

  @Query("""
  SELECT COUNT(rl)
    FROM ReviewLike rl
   WHERE rl.review.user.id = :userId
     AND rl.liked = true
     AND rl.createdAt BETWEEN :start AND :end
""")
  long countLikesByUserAndPeriod(
      @Param("userId") UUID userId,
      @Param("start") Instant start,
      @Param("end")   Instant end
  );

  @Query("""
    SELECT COALESCE(SUM(r.rating), 0)
      FROM Review r
     WHERE r.user.id = :userId
       AND r.createdAt BETWEEN :start AND :end
  """)
  double sumRatingByUserAndPeriod(
      @Param("userId") UUID userId,
      @Param("start") Instant start,
      @Param("end")   Instant end
  );
}
