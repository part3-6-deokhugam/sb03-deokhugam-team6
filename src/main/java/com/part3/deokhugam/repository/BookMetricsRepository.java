package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.BookMetrics;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookMetricsRepository extends JpaRepository<BookMetrics, UUID> {
}
