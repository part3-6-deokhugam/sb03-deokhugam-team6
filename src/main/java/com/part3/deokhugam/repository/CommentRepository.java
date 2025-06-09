package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, UUID>, CommentRepositoryCustom {

  @Query("SELECT COUNT(c) FROM Comment c WHERE c.review.id = :reviewId AND c.deleted = false")
  long countByReviewId(UUID reviewId);
}
