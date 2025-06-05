package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.Book;
import com.part3.deokhugam.domain.QBook;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Book> findByIdNotDelete(UUID id) {
        QBook book = QBook.book;

        Book result = queryFactory
                .selectFrom(book)
                .where(book.id.eq(id).and(book.deleted.isFalse()))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<Book> findListByCursor(String keyword, Instant after, String cursor, String orderBy,
                                       String direction, int limit) {

        QBook book = QBook.book;
        BooleanBuilder predicate = new BooleanBuilder();

        // 1. 키워드 검색 조건 (부분일치)
        if (keyword != null && !keyword.isEmpty()) {
            predicate.and(
                    book.title.likeIgnoreCase("%" + keyword + "%")
                            .or(book.author.likeIgnoreCase("%" + keyword + "%"))
                            .or(book.isbn.likeIgnoreCase("%" + keyword + "%"))
            );
        } else {
            predicate.and(book.isNotNull()); // 항상 true가 되는 조건
        }

        // 2. 논리 삭제 안 된 도서만
        predicate.and(book.deleted.isFalse());

        // 3. 커서 조건 인라인 처리
        if (cursor != null && after != null) {
            switch (orderBy) {
                case "title" -> {
                    if ("asc".equalsIgnoreCase(direction)) {
                        predicate.and(
                                book.title.gt(cursor)
                                        .or(book.title.eq(cursor).and(book.createdAt.gt(after)))
                        );
                    } else {
                        predicate.and(
                                book.title.lt(cursor)
                                        .or(book.title.eq(cursor).and(book.createdAt.lt(after)))
                        );
                    }
                }
                case "publishedDate" -> {
                    LocalDate date = LocalDate.parse(cursor);
                    if ("asc".equalsIgnoreCase(direction)) {
                        predicate.and(
                                book.publishedDate.gt(date)
                                        .or(book.publishedDate.eq(date).and(book.createdAt.gt(after)))
                        );
                    } else {
                        predicate.and(
                                book.publishedDate.lt(date)
                                        .or(book.publishedDate.eq(date).and(book.createdAt.lt(after)))
                        );
                    }
                }
                case "rating" -> {
                    BigDecimal rating = new BigDecimal(cursor);
                    if ("asc".equalsIgnoreCase(direction)) {
                        predicate.and(
                                book.bookMetrics.averageRating.gt(rating)
                                        .or(book.bookMetrics.averageRating.eq(rating).and(book.createdAt.gt(after)))
                        );
                    } else {
                        predicate.and(
                            book.bookMetrics.averageRating.lt(rating)
                                        .or(book.bookMetrics.averageRating.eq(rating).and(book.createdAt.lt(after)))
                        );
                    }
                }
                case "reviewCount" -> {
                    int reviewCount = Integer.parseInt(cursor);
                    if ("asc".equalsIgnoreCase(direction)) {
                        predicate.and(
                                book.bookMetrics.reviewCount.gt(reviewCount)
                                        .or( book.bookMetrics.reviewCount.eq(reviewCount).and(book.createdAt.gt(after)))
                        );
                    } else {
                        predicate.and(
                            book.bookMetrics.reviewCount.lt(reviewCount)
                                        .or(book.bookMetrics.reviewCount.eq(reviewCount).and(book.createdAt.lt(after)))
                        );
                    }
                }
            }
        }

        // 4. 정렬 조건 정의
        OrderSpecifier<?> primaryOrder;
        OrderSpecifier<?> secondaryOrder = "asc".equalsIgnoreCase(direction)
                ? book.createdAt.asc()
                : book.createdAt.desc();

        switch (orderBy) {
            case "title" -> primaryOrder = "asc".equalsIgnoreCase(direction) ? book.title.asc() : book.title.desc();
            case "publishedDate" -> primaryOrder = "asc".equalsIgnoreCase(direction) ? book.publishedDate.asc() : book.publishedDate.desc();
            case "rating" -> primaryOrder = "asc".equalsIgnoreCase(direction) ? book.bookMetrics.averageRating.asc() : book.bookMetrics.averageRating.desc();
            case "reviewCount" -> primaryOrder = "asc".equalsIgnoreCase(direction) ? book.bookMetrics.reviewCount.asc() : book.bookMetrics.reviewCount.desc();
            default -> throw new IllegalArgumentException("정렬 기준이 잘못되었습니다: " + orderBy);
        }

        // 5. 쿼리 실행
        return queryFactory
                .selectFrom(book)
                .where(predicate)
                .orderBy(primaryOrder, secondaryOrder)
                .limit(limit)
                .fetch();
    }

    @Override
    public Long getTotalElements(String keyword) {
        QBook book = QBook.book;
        BooleanBuilder predicate = new BooleanBuilder();

        predicate.and(book.deleted.isFalse());

        if (keyword != null && !keyword.isEmpty()) {
            predicate.and(
                    book.title.containsIgnoreCase(keyword)
                            .or(book.author.containsIgnoreCase(keyword))
                            .or(book.isbn.containsIgnoreCase(keyword))
            );
        }

        return queryFactory
                .select(book.count())
                .from(book)
                .where(predicate)
                .fetchOne();
    }
}
