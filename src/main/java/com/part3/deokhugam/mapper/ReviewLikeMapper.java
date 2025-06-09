package com.part3.deokhugam.mapper;

import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.domain.ReviewLike;
import com.part3.deokhugam.domain.ReviewLikeId;
import com.part3.deokhugam.domain.User;
import com.part3.deokhugam.dto.review.ReviewLikeDto;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewLikeMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", source = "user")
  @Mapping(target = "review", source = "review")
  @Mapping(target = "liked", source = "liked")
  @Mapping(target = "createdAt", ignore = true)
  ReviewLike toReviewLikeWithoutId(User user, Review review, boolean liked);

  default ReviewLike toReviewLike(User user, Review review, boolean liked) {
    ReviewLike result = toReviewLikeWithoutId(user, review, liked);
    result.setId(new ReviewLikeId(review.getId(), user.getId()));
    return result;
  }

  ReviewLikeDto toReviewLikeDto(UUID userId, UUID reviewId, boolean liked);
}
