package com.part3.deokhugam.service;

import com.part3.deokhugam.domain.UserRankData;
import com.part3.deokhugam.domain.enums.Period;
import com.part3.deokhugam.dto.pagination.CursorPageResponseUserRankDataDto;
import com.part3.deokhugam.dto.user.UserRankDataDto;
import com.part3.deokhugam.repository.UserRankDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRankDataService {

  private final UserRankDataRepository userRankDataRepository;

  public CursorPageResponseUserRankDataDto getPowerUsersCursor(
      Period period,
      LocalDate periodDate,
      String cursor,
      Instant after,
      String direction,
      int limit
  ) {
    // 1) 올바른 direction 검증
    boolean asc = "ASC".equalsIgnoreCase(direction);
    boolean desc = "DESC".equalsIgnoreCase(direction);
    if (!asc && !desc) {
      throw new IllegalArgumentException("direction 값은 ASC 또는 DESC 이어야 합니다. 입력값: " + direction);
    }

    // (A) 1차: period + periodDate 에 해당하는 전체 List<UserRankData>를 가져온다.
    List<UserRankData> allEntities = userRankDataRepository
        .findByPeriodAndPeriodDateOrderByRankAsc(period, periodDate);

    // (B) 2차: direction(ASC/DESC)에 맞게 정렬 순서를 변경
    List<UserRankData> sorted;
    if (asc) {
      // 이미 findByPeriodAndPeriodDateOrderByRankAsc로 오름차순 정렬되어 있으므로 그대로 사용
      sorted = new ArrayList<>(allEntities);
    } else {
      // 내림차순일 때는 리스트를 역순으로 뒤집기
      sorted = new ArrayList<>(allEntities);
      Collections.reverse(sorted);
    }

    // (C) 3차: cursor + after 에 따라 “이번 페이지에 포함될 개별 Entity”를 필터링
    List<UserRankData> pageEntities = new ArrayList<>();

    for (UserRankData entity : sorted) {
      // 1) cursor가 비어있으면 첫 페이지
      // 2) cursor가 있으면, 랭킹 값 비교
      boolean skip = false;
      if (cursor != null && !cursor.isEmpty()) {
        int rankCursor = Integer.parseInt(cursor);

        if (asc) {
          // 오름차순: rank > rankCursor 이어야 이번 페이지에 포함
          if (entity.getRank() <= rankCursor) {
            skip = true;
          }
        } else {
          // 내림차순: rank < rankCursor 이어야 이번 페이지에 포함
          if (entity.getRank() >= rankCursor) {
            skip = true;
          }
        }
      }

      // 3) 같은 rank(동점)인 경우, createdAt 비교 (after 보조 커서)
      if (!skip && after != null) {
        Instant createdAt = entity.getCreatedAt();
        if (asc) {
          // 오름차순 + rank가 같다면 createdAt > after 이어야 함
          if (!createdAt.isAfter(after) && entity.getRank() == Integer.parseInt(cursor)) {
            skip = true;
          }
        } else {
          // 내림차순 + rank가 같다면 createdAt < after 이어야 함
          if (!createdAt.isBefore(after) && entity.getRank() == Integer.parseInt(cursor)) {
            skip = true;
          }
        }
      }

      if (!skip) {
        pageEntities.add(entity);
        // 한 페이지 분량(limit+1)를 넘으면 루프 중단
        if (pageEntities.size() >= limit + 1) {
          break;
        }
      }
    }

    // (D) pageEntities.size() > limit이면, hasNext = true 이므로
    boolean hasNext = pageEntities.size() > limit;

    // (E) 실제로 반환할 엔티티 목록 (limit개만)
    List<UserRankData> actualPage = hasNext
        ? pageEntities.subList(0, limit)
        : pageEntities;

    // (F) Entity → DTO 로 매핑
    List<UserRankDataDto> dtos = actualPage.stream()
        .map(entity -> new UserRankDataDto(
            entity.getUser().getId(),
            entity.getUser().getNickname(),
            entity.getPeriodType(),
            entity.getCreatedAt(),           // BaseEntity에서 상속받은 createdAt
            entity.getRank(),
            entity.getScore(),
            entity.getReviewScoreSum(),
            entity.getPeriodDate(),
            entity.getLikeCount(),
            entity.getCommentCount()
        ))
        .collect(Collectors.toList());

    // (G) 다음 페이지를 위한 nextCursor / nextAfter 계산
    String nextCursor = null;
    Instant nextAfter = null;

    if (hasNext) {
      UserRankData last = pageEntities.get(limit); // limit+1번째 아이템
      nextCursor = String.valueOf(last.getRank());
      nextAfter = last.getCreatedAt();
    }

    // (H) 전체 Elements 수 조회 (카운트 쿼리)
    long totalElements = userRankDataRepository.countByPeriodAndPeriodDate(period, periodDate);

    return new CursorPageResponseUserRankDataDto(
        dtos,
        nextCursor,
        nextAfter,
        dtos.size(),
        totalElements,
        hasNext
    );
  }
}