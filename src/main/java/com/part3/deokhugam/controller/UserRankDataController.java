package com.part3.deokhugam.controller;

import com.part3.deokhugam.domain.enums.Period;
import com.part3.deokhugam.dto.user.UserRankDataDto;
import com.part3.deokhugam.service.UserRankDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "파워 유저", description = "기간별 파워 유저 랭킹 조회 API")
@RestController
@RequestMapping("/api/users/power")
@RequiredArgsConstructor
@Validated
public class UserRankDataController {

  private final UserRankDataService userRankDataService;

  @Operation(summary = "파워 유저 목록 조회",
      description = "기간(DAILY, WEEKLY, MONTHLY, ALL_TIME)과 날짜(periodDate) 기준으로 파워 유저 순위를 조회합니다.")
  @GetMapping
  public ResponseEntity<List<UserRankDataDto>> getPowerUsers(
      @RequestParam(name = "period", defaultValue = "DAILY")
      @Pattern(regexp = "DAILY|WEEKLY|MONTHLY|ALL_TIME", message = "period 값이 올바르지 않습니다.")
      String period,

      @RequestParam(name = "periodDate",
          required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      LocalDate periodDate
  ) {
    Period enumPeriod = Period.valueOf(period);

    LocalDate dateToUse = (periodDate != null) ? periodDate : LocalDate.now();

    List<UserRankDataDto> dtos = userRankDataService.getUserRanks(enumPeriod, dateToUse);
    return ResponseEntity.ok(dtos);
  }
}