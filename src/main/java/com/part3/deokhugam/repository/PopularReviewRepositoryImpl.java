package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.PopularReview;
import com.part3.deokhugam.domain.QBook;
import com.part3.deokhugam.domain.QPopularReview;
import com.part3.deokhugam.domain.QReview;
import com.part3.deokhugam.domain.QUser;
import com.part3.deokhugam.domain.enums.Period;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PopularReviewRepositoryImpl implements PopularReviewRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<PopularReview> findPopularReviewsWithCursor(
      Period period,
      String direction,
      Integer cursorRank,
      Instant after,
      int limit
  ) {
    QPopularReview pr = QPopularReview.popularReview;
    QReview r = QReview.review;
    QBook b = QBook.book;
    QUser u = QUser.user;

    // 기본 조건
    BooleanBuilder builder = new BooleanBuilder();
    builder.and(pr.periodType.eq(period));

    // 보조 커서(createdAt)
    if (after != null) {
      builder.and(pr.createdAt.loe(after));
    }

    if (cursorRank != null) {
      if ("ASC".equalsIgnoreCase(direction)) {
        builder.and(pr.rank.gt(cursorRank));
      } else {
        builder.and(pr.rank.lt(cursorRank));
      }
    }

    OrderSpecifier<?> orderBy = "ASC".equalsIgnoreCase(direction)
        ? pr.rank.asc()
        : pr.rank.desc();

    return queryFactory
        .selectFrom(pr)
        .leftJoin(pr.review, r).fetchJoin()
        .leftJoin(r.book, b).fetchJoin()
        .leftJoin(r.user, u).fetchJoin()
        .where(builder)
        .orderBy(orderBy, pr.createdAt.desc())
        .limit(limit)
        .fetch();
  }
}

