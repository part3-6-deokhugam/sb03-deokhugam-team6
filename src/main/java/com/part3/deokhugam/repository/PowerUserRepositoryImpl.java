package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.PowerUser;
import com.part3.deokhugam.domain.QPowerUser;
import com.part3.deokhugam.domain.enums.Period;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PowerUserRepositoryImpl implements PowerUserRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<PowerUser> findByPeriodWithCursor(
      Period period,
      LocalDate periodDate,
      String direction,
      String cursor,
      Instant after,
      int limit
  ) {
    QPowerUser urd = QPowerUser.powerUser;
    BooleanBuilder cond = new BooleanBuilder()
        .and(urd.periodType.eq(period))
        .and(urd.periodDate.eq(periodDate));

    if (cursor != null && after != null) {
      // cursor → last rank or createdAt 값을 기준으로 필터링
      cond.and(
          "DESC".equalsIgnoreCase(direction)
              ? urd.createdAt.lt(after)
              : urd.createdAt.gt(after)
      );
    }

    OrderSpecifier<?> order = "DESC".equalsIgnoreCase(direction)
        ? urd.rank.desc()
        : urd.rank.asc();

    return queryFactory
        .selectFrom(urd)
        .where(cond)
        .orderBy(order)
        .limit(limit + 1)
        .fetch();
  }

  @Override
  public long countByPeriod(Period period, LocalDate periodDate) {
    QPowerUser urd = QPowerUser.powerUser;
    return queryFactory
        .selectFrom(urd)
        .where(urd.periodType.eq(period)
            .and(urd.periodDate.eq(periodDate)))
        .fetchCount();
  }
}