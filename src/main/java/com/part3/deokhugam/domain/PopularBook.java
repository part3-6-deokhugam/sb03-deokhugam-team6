package com.part3.deokhugam.domain;

import com.part3.deokhugam.domain.enums.Period;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
@Entity
@Table(name = "popular_books")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PopularBook extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column( nullable = false)
    @Enumerated(EnumType.STRING)
    private Period period;

    @Column(nullable = false)
    private LocalDate periodDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal score;

    @Column(nullable = false)
    private Integer reviewCount;

    @Column(nullable = false)
    private Integer rank;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Builder
    public PopularBook(Period period, LocalDate periodDate, BigDecimal score, Integer reviewCount, Book book) {
        this.period = period;
        this.periodDate = periodDate;
        this.score = score;
        this.reviewCount = reviewCount;
        this.book = book;
    }
}
