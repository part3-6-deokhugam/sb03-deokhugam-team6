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
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(source = "user",              target = "user")
    @Mapping(source = "review",            target = "review")
    @Mapping(source = "request.content",   target = "content")
    Comment toEntity(CommentCreateRequest request, User user, Review review);

    @Mapping(source = "id",                target = "id")
    @Mapping(source = "user.id",           target = "userId")
    @Mapping(source = "review.id",         target = "reviewId")
    @Mapping(source = "user.nickname",     target = "userNickname")
    @Mapping(source = "content",           target = "content")
    @Mapping(source = "createdAt",         target = "createdAt")
    @Mapping(source = "updatedAt",         target = "updatedAt")
    CommentDto toDto(Comment comment);
}
