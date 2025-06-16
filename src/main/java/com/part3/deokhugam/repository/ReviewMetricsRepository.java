package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.domain.ReviewMetrics;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewMetricsRepository extends JpaRepository<ReviewMetrics, UUID> {
  @Query("""
      SELECT m.review
        FROM ReviewMetrics m
       WHERE m.updatedAt BETWEEN :start AND :end
       ORDER BY m.likeCount DESC
      """)
  List<Review> findTop10ReviewsByLikesInPeriod(
      @Param("start") Instant start,
      @Param("end")   Instant end,
      Pageable pageable
  );
}
