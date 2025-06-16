package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.ReviewLike;
import com.part3.deokhugam.domain.ReviewLikeId;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, ReviewLikeId> {

  Optional<ReviewLike> findByReview_IdAndUser_Id(UUID reviewId, UUID userId);
}
