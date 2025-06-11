package com.part3.deokhugam.mapper;

import com.part3.deokhugam.domain.PopularReview;
import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.domain.enums.Period;
import com.part3.deokhugam.dto.review.PopularReviewDto;
import java.time.LocalDate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PopularReviewMapper {

  @Mapping(target="id", ignore=true)
  @Mapping(target="rank", ignore=true)
  PopularReview toPopularReview(Review review, Period periodType, LocalDate periodDate, double score, int likeCount, int commentCount);

  @Mapping(target="id", source="popularReview.id")
  @Mapping(target="reviewId", source="popularReview.review.id")
  @Mapping(target="bookId", source="popularReview.review.book.id")
  @Mapping(target="bookTitle", source="popularReview.review.book.title")
  @Mapping(target="bookThumbnailUrl", source="popularReview.review.book.thumbnailUrl")
  @Mapping(target="userId", source="popularReview.review.user.id")
  @Mapping(target="userNickname", source="popularReview.review.user.nickname")
  @Mapping(target="reviewContent", source="popularReview.review.content")
  @Mapping(target="reviewRating", source="popularReview.review.rating")
  @Mapping(target="period", source="popularReview.periodType")
  PopularReviewDto toDto(PopularReview popularReview);
}
