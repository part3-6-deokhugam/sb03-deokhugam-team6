package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.PopularBook;
import com.part3.deokhugam.domain.QPopularBook;
import com.part3.deokhugam.domain.enums.Period;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PopularBookRepositoryImpl implements PopularBookRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<PopularBook> findListByCursor(String period, Instant after, String cursor, String direction, int limit) {
        QPopularBook popularBook = QPopularBook.popularBook;
        BooleanBuilder predicate = new BooleanBuilder();

        predicate.and(popularBook.period.eq(Period.valueOf(period)));
        predicate.and(popularBook.periodDate.eq(LocalDate.now().minusDays(1)));

        OrderSpecifier<?> orderSpecifier = direction.equalsIgnoreCase("DESC") ?
                popularBook.rank.desc() : popularBook.rank.asc();

        OrderSpecifier<?> secondaryOrderSpecifier = direction.equalsIgnoreCase("asc") ?
                popularBook.createdAt.asc() : popularBook.createdAt.desc();

        if (cursor != null && after != null) {
            int cursor_rank = Integer.parseInt(cursor);
            BooleanBuilder cursorCondition = new BooleanBuilder();

            if ("ASC".equalsIgnoreCase(direction)) {
                cursorCondition.or(popularBook.rank.gt(cursor_rank));
                cursorCondition.or(popularBook.rank.eq(cursor_rank).and(popularBook.createdAt.gt(after)));
            } else {
                cursorCondition.or(popularBook.rank.lt(cursor_rank));
                cursorCondition.or(popularBook.rank.eq(cursor_rank).and(popularBook.createdAt.lt(after)));
            }

            predicate.and(cursorCondition);
        }

        return queryFactory.selectFrom(popularBook)
                .where(predicate)
                .orderBy(orderSpecifier, secondaryOrderSpecifier)
                .limit(limit)
                .fetch();
    }

    @Override
    public Long getTotalElements(String period) {
        QPopularBook popularBook = QPopularBook.popularBook;
        BooleanBuilder predicate = new BooleanBuilder();

        predicate.and(popularBook.period.eq(Period.valueOf(period)));

        return queryFactory
                .select(popularBook.count())
                .from(popularBook)
                .where(predicate)
                .fetchOne();
    }
}
