package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.Comment;
import com.part3.deokhugam.domain.QComment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Comment> findByReviewIdWithCursor(UUID reviewId, String direction, String cursor,
      Instant after, int limit) {
    QComment comment = QComment.comment;

    BooleanBuilder condition = new BooleanBuilder();
    condition.and(comment.review.id.eq(reviewId));
    condition.and(comment.deleted.isFalse());

    if (cursor != null && after != null) {
      switch (direction) {
        case "DESC" -> condition.and(comment.createdAt.lt(after));
        case "ASC" -> condition.and(comment.createdAt.gt(after));
      }
    }

    OrderSpecifier<?> orderSpecifier = "DESC".equals(direction)
        ? comment.createdAt.desc()
        : comment.createdAt.asc();

    return queryFactory
        .selectFrom(comment)
        .where(condition)
        .orderBy(orderSpecifier)
        .limit(limit)
        .fetch();
  }
}
