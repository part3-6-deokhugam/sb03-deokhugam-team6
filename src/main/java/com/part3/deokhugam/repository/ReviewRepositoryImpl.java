package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.QBook;
import com.part3.deokhugam.domain.QReview;
import com.part3.deokhugam.domain.QUser;
import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.dto.review.ReviewSearchCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

  @PersistenceContext
  private EntityManager em;

  @Override
  public List<Review> findAll(ReviewSearchCondition condition) {
    QReview review = QReview.review;
    QUser user = QUser.user;
    QBook book = QBook.book;

    JPAQuery<Review> query = new JPAQueryFactory(em)
        .selectFrom(review)
        .join(review.user, user).fetchJoin()
        .join(review.book, book).fetchJoin();

    BooleanBuilder where = new BooleanBuilder();

    where.and(review.deleted.isFalse());

    if (condition.getUserId() != null) {
      where.and(review.user.id.eq(condition.getUserId()));
    }
    if (condition.getBookId() != null) {
      where.and(review.book.id.eq(condition.getBookId()));
    }
    if (condition.getKeyword() != null) {
      String keyword = "%" + condition.getKeyword().toLowerCase() + "%";
      where.and(
          review.content.lower().like(keyword)
              .or(user.nickname.lower().like(keyword))
              .or(book.title.lower().like(keyword))
              .or(review.content.lower().like(keyword))
      );
    }

    String orderBy = condition.getOrderBy();
    String cursor = condition.getCursor();
    Instant after = condition.getAfter();

    if ("rating".equals(orderBy)) {
      if (cursor != null && after != null) {
        int ratingCursor = (int) Double.parseDouble(cursor);
        where.and(
            review.rating.lt(ratingCursor)
                .or(review.rating.eq(ratingCursor))
                .and(review.createdAt.lt(after))
        );
      }
      query.orderBy(review.rating.desc(), review.createdAt.desc());
    } else {
      if (after != null) {
        where.and(review.createdAt.lt(after));
      }
      query.orderBy(review.createdAt.desc());
    }

    int limit = condition.getLimit();

    query.where(where)
        .limit(limit + 1);

    return query.fetch();
  }

  public long countByCondition(ReviewSearchCondition condition) {
    QReview review = QReview.review;

    BooleanBuilder where = new BooleanBuilder();

    if (condition.getUserId() != null) {
      where.and(review.user.id.eq(condition.getUserId()));
    }
    if (condition.getBookId() != null) {
      where.and(review.book.id.eq(condition.getBookId()));
    }
    if (condition.getKeyword() != null) {
      String keyword = "%" + condition.getKeyword().toLowerCase() + "%";
      where.and(
          review.content.lower().like(keyword)
              .or(review.user.nickname.lower().like(keyword))
              .or(review.book.title.lower().like(keyword))
              .or(review.book.description.lower().like(keyword))
      );
    }

    Long res = new JPAQueryFactory(em)
        .select(review.count())
        .from(review)
        .where(where)
        .fetchOne();

    return res != null ? res : 0;
  }
}