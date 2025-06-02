package com.part3.deokhugam.mapper;

import com.part3.deokhugam.domain.Comment;
import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.domain.User;
import com.part3.deokhugam.dto.comment.CommentCreateRequest;
import com.part3.deokhugam.dto.comment.CommentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toEntity(CommentCreateRequest request, User user, Review review);

    @Mapping(source = "user.id" , target = "userId")
    @Mapping(source = "review.id", target = "reviewId")
    @Mapping(source = "user.nickname", target = "userNickname")
    CommentDto toDto(Comment comment);
}
