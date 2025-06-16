package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.Comment;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, UUID>, CommentRepositoryCustom {

  @Query("SELECT COUNT(c) FROM Comment c WHERE c.review.id = :reviewId AND c.deleted = false")
  long countByReviewId(UUID reviewId);

  @Query("""
  SELECT COUNT(c)
    FROM Comment c
   WHERE c.user.id = :userId
     AND c.createdAt BETWEEN :start AND :end
""")
  long countByUserAndPeriod(
      @Param("userId") UUID userId,
      @Param("start") Instant start,
      @Param("end")   Instant end
  );
}
