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
        case "desc" -> condition.and(comment.createdAt.lt(after));
        case "asc" -> condition.and(comment.createdAt.gt(after));
        default -> throw new IllegalArgumentException("지원하지 않는 정렬 방향입니다");
      }
    }

    OrderSpecifier<?> orderSpecifier = switch (direction) {
      case "desc" -> comment.createdAt.desc();
      case "asc" -> comment.createdAt.asc();
      default -> throw new IllegalArgumentException("지원하지 않는 정렬 방향입니다.");
    };

    return queryFactory
        .selectFrom(comment)
        .where(condition)
        .orderBy(orderSpecifier)
        .limit(limit)
        .fetch();
  }
}
