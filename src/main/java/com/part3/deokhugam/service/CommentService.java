package com.part3.deokhugam.service;

import com.part3.deokhugam.domain.Comment;
import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.domain.User;
import com.part3.deokhugam.dto.comment.CommentCreateRequest;
import com.part3.deokhugam.dto.comment.CommentDto;
import com.part3.deokhugam.dto.comment.CommentUpdateRequest;
import com.part3.deokhugam.dto.pagination.CursorPageResponseCommentDto;
import com.part3.deokhugam.exception.CommentException;
import com.part3.deokhugam.exception.ErrorCode;
import com.part3.deokhugam.exception.ReviewException;
import com.part3.deokhugam.exception.UserException;
import com.part3.deokhugam.mapper.CommentMapper;
import com.part3.deokhugam.repository.CommentRepository;
import com.part3.deokhugam.repository.ReviewRepository;
import com.part3.deokhugam.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final ReviewRepository reviewRepository;
  private final CommentMapper commentMapper;

  @Transactional
  public CommentDto create(CommentCreateRequest request) {
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND,
            Map.of("userId", request.getUserId().toString())));
    Review review = reviewRepository.findById(request.getReviewId())
        .orElseThrow(() -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND,
            Map.of("reviewId", request.getReviewId().toString())));

    Comment comment = commentMapper.toEntity(request, user, review);
    Comment savedComment = commentRepository.save(comment);

    return commentMapper.toDto(savedComment);
  }

  @Transactional(readOnly = true)
  public CommentDto findById(UUID commentId) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND,
            Map.of("commentId", commentId.toString())));
    return commentMapper.toDto(comment);
  }

  @Transactional(readOnly = true)
  public CursorPageResponseCommentDto findAll(UUID reviewId, String direction, String cursor,
      Instant after,
      int limit) {
    reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND,
            Map.of("reviewId", reviewId.toString())));

    List<Comment> comments = commentRepository.findByReviewIdWithCursor(reviewId, direction, cursor,
        after, limit + 1);

    boolean hasNext = comments.size() > limit;
    List<Comment> pagedComments = hasNext ? comments.subList(0, limit) : comments;

    List<CommentDto> content = pagedComments.stream()
        .map(commentMapper::toDto)
        .toList();

    String nextCursor =
        hasNext ? pagedComments.get(pagedComments.size() - 1).getId().toString() : null;
    Instant nextAfter = hasNext ? pagedComments.get(pagedComments.size() - 1).getCreatedAt() : null;

    long totalElements = commentRepository.countByReviewId(reviewId);

    return new CursorPageResponseCommentDto(content, nextCursor, nextAfter, pagedComments.size(),
        totalElements, hasNext);
  }

  @Transactional
  public CommentDto update(UUID commentId, UUID userId, CommentUpdateRequest request) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND,
            Map.of("commentId", commentId.toString())));
    if (!comment.getUser().getId().equals(userId)) {
      throw new CommentException(ErrorCode.COMMENT_FORBIDDEN,
          Map.of(
              "commentId", commentId.toString(),
              "userId", userId.toString()
          ));
    }
    comment.update(request.getContent());
    return commentMapper.toDto(comment);

  }

  @Transactional
  public void deleteLogically(UUID commentId, UUID userId) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND,
            Map.of("commentId", commentId.toString())));
    if (!comment.getUser().getId().equals(userId)) {
      throw new CommentException(ErrorCode.COMMENT_FORBIDDEN,
          Map.of(
              "commentId", commentId.toString(),
              "userId", userId.toString()
          ));
    }
    comment.markAsDeleted();
  }

  @Transactional
  public void deletePhysically(UUID commentId, UUID userId) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND,
            Map.of("commentId", commentId.toString())));
    if (!comment.getUser().getId().equals(userId)) {
      throw new CommentException(ErrorCode.COMMENT_FORBIDDEN,
          Map.of(
              "commentId", commentId.toString(),
              "userId", userId.toString()
          ));
    }
    commentRepository.delete(comment);
  }
}
