package com.part3.deokhugam.batch;

import com.part3.deokhugam.domain.enums.Period;
import com.part3.deokhugam.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PopularBookScheduler {
    private final BookService bookService;

    // 매일 자정 배치 연산 수행
    @Scheduled(cron = "${scheduler.batch.start-time}")
    public void updateRanking() {
        for (Period period : Period.values()) {
            bookService.calculateRanking(period);
        }
    }
}
