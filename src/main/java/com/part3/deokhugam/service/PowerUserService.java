package com.part3.deokhugam.service;

import com.part3.deokhugam.domain.PowerUser;
import com.part3.deokhugam.domain.enums.Period;
import com.part3.deokhugam.dto.pagination.CursorPageResponsePowerUserDto;
import com.part3.deokhugam.dto.user.PowerUserDto;
import com.part3.deokhugam.repository.PowerUserRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PowerUserService {
  private final PowerUserRepository repo;

  public CursorPageResponsePowerUserDto getPowerUsersCursor(
      Period period,
      LocalDate date,
      String direction,
      String cursor,
      Instant after,
      int limit
  ) {
    // 1) 데이터를 limit+1개 조회
    List<PowerUser> all = repo.findByPeriodWithCursor(period, date, direction, cursor, after, limit);

    // 2) 페이징 처리
    boolean hasNext = all.size() > limit;
    List<PowerUser> page = hasNext ? all.subList(0, limit) : all;

    // 3) DTO 변환
    List<PowerUserDto> content = page.stream()
        .map(PowerUserDto::fromEntity)
        .toList();

    // 4) nextCursor/nextAfter 계산
    String nextCursor = hasNext ? page.get(page.size()-1).getRank().toString() : null;
    Instant nextAfter = hasNext ? page.get(page.size()-1).getCreatedAt() : null;

    // 5) totalElements
    long total = repo.countByPeriod(period, date);

    return new CursorPageResponsePowerUserDto(
        content, nextCursor, nextAfter, content.size(), total, hasNext
    );
  }
}