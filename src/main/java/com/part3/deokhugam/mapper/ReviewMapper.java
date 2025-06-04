package com.part3.deokhugam.mapper;


import com.part3.deokhugam.domain.Book;
import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.domain.User;
import com.part3.deokhugam.dto.review.ReviewCreateRequest;
import com.part3.deokhugam.dto.review.ReviewDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

  Review toReview(ReviewCreateRequest request, User user, Book book);


  @Mapping(target = "bookId", source = "book.id")
  @Mapping(target = "bookTitle", source = "book.title")
  @Mapping(target = "bookThumbnailUrl", source = "book.thumbnailUrl")
  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "userNickname", source = "user.nickname")
//  @Mapping(target = "likeCount", expression = "java(review.getMetrics() != null ? review.getMetrics().getLikeCount() : 0)")
//  @Mapping(target = "commentCount", expression = "java(review.getMetrics() != null ? review.getMetrics().getCommentCount() : 0)")
//  @Mapping(target = "likedByMe", ignore = true)
//
//  @Mapping(source="user.id",target="userId")
//  @Mapping(source="book.id", target="bookId")
//  @Mapping(source="reviewLike.liked", target="likedByMe")
// 관련 엔티티 생성 후 수정
  ReviewDto toDto(Review review);

}
