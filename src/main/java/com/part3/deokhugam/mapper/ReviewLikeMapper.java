package com.part3.deokhugam.mapper;

import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.domain.ReviewLike;
import com.part3.deokhugam.domain.User;
import com.part3.deokhugam.dto.review.ReviewLikeDto;
import java.util.UUID;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewLikeMapper {

  ReviewLike toReviewLike(User user, Review review, boolean liked);

  ReviewLikeDto toReviewLikeDto(UUID userId, UUID reviewId, boolean liked);
}
