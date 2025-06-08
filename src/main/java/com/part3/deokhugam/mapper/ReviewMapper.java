package com.part3.deokhugam.mapper;


import com.part3.deokhugam.domain.Book;
import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.domain.ReviewMetrics;
import com.part3.deokhugam.domain.User;
import com.part3.deokhugam.dto.review.ReviewCreateRequest;
import com.part3.deokhugam.dto.review.ReviewDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "rating", source = "request.rating")
  Review toReview(ReviewCreateRequest request, User user, Book book);

  @Mapping(target = "bookId", source = "review.book.id")
  @Mapping(target = "bookTitle", source = "review.book.title")
  @Mapping(target = "bookThumbnailUrl", source = "review.book.thumbnailUrl")
  @Mapping(target = "userId", source = "review.user.id")
  @Mapping(target = "userNickname", source = "review.user.nickname")
  @Mapping(target = "likedByMe", ignore = true)
  @Mapping(target = "createdAt", source = "review.createdAt")
  @Mapping(target = "updatedAt", source = "review.updatedAt")
  ReviewDto toDto(Review review, ReviewMetrics reviewMetrics);

  @Mapping(target = "bookId", source = "review.book.id")
  @Mapping(target = "bookTitle", source = "review.book.title")
  @Mapping(target = "bookThumbnailUrl", source = "review.book.thumbnailUrl")
  @Mapping(target = "userId", source = "review.user.id")
  @Mapping(target = "userNickname", source = "review.user.nickname")
  @Mapping(target = "createdAt", source = "review.createdAt")
  @Mapping(target = "updatedAt", source = "review.updatedAt")
  ReviewDto toDto(Review review, ReviewMetrics reviewMetrics, Boolean likedByMe);

}
