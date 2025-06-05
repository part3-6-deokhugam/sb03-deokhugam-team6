package com.part3.deokhugam.controller;

import com.part3.deokhugam.domain.enums.Period;
import com.part3.deokhugam.dto.pagination.CursorPageResponseUserRankDataDto;
import com.part3.deokhugam.service.UserRankDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;

@Tag(name = "파워 유저", description = "기간별 파워 유저 랭킹 조회 API")
@RestController
@RequestMapping("/api/users/power")
@RequiredArgsConstructor
@Validated
public class UserRankDataController {

  private final UserRankDataService userRankDataService;

  @Operation(summary = "파워 유저 목록 조회",
      description = "기간별(DAILY, WEEKLY, MONTHLY, ALL_TIME) 파워 유저 랭킹을 커서 기반으로 조회합니다.")
  @GetMapping
  public ResponseEntity<CursorPageResponseUserRankDataDto> getPowerUsers(
      @RequestParam(name = "period", defaultValue = "DAILY")
      @Pattern(regexp = "DAILY|WEEKLY|MONTHLY|ALL_TIME", message = "period 값이 올바르지 않습니다.")
      String period,

      @RequestParam(name = "periodDate", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      LocalDate periodDate,

      @RequestParam(name = "direction", defaultValue = "ASC")
      @Schema(allowableValues = {"ASC", "DESC"})
      String direction,

      @RequestParam(name = "cursor", required = false)
      String cursor,

      @RequestParam(name = "after", required = false)
      Instant after,

      @RequestParam(name = "limit", defaultValue = "50")
      int limit
  ) {
    // 1) Enum 변환 (잘못된 값이 넘어오면 IllegalArgumentException 발생)
    Period enumPeriod = Period.valueOf(period);

    // 2) periodDate가 없으면 “오늘 날짜” 기준으로 사용
    LocalDate dateToUse = (periodDate != null) ? periodDate : LocalDate.now();

    // 3) Service 호출
    CursorPageResponseUserRankDataDto responseDto = userRankDataService.getPowerUsersCursor(
        enumPeriod,
        dateToUse,
        cursor,
        after,
        direction,
        limit
    );

    return ResponseEntity.ok(responseDto);
  }
}