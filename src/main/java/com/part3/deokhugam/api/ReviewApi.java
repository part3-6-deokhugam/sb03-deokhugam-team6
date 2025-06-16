package com.part3.deokhugam.api;

import com.part3.deokhugam.dto.pagination.CursorPageResponsePopularReviewDto;
import com.part3.deokhugam.dto.pagination.CursorPageResponseReviewDto;
import com.part3.deokhugam.dto.review.PopularReviewSearchCondition;
import com.part3.deokhugam.dto.review.ReviewCreateRequest;
import com.part3.deokhugam.dto.review.ReviewDto;
import com.part3.deokhugam.dto.review.ReviewLikeDto;
import com.part3.deokhugam.dto.review.ReviewSearchCondition;
import com.part3.deokhugam.dto.review.ReviewUpdateRequest;
import com.part3.deokhugam.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "리뷰 관리", description = "리뷰 관련 API")
@RequestMapping("/api/reviews")
public interface ReviewApi {

  @Operation(
      summary = "리뷰 목록 조회",
      description = "검색 조건에 맞는 리뷰 목록을 조회합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공",
          content = @Content(schema = @Schema(implementation = CursorPageResponseReviewDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (정렬 기준 오류, 페이지네이션 파라미터 오류, 요청자 ID 누락)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping
  ResponseEntity<CursorPageResponseReviewDto> getReviews(
      ReviewSearchCondition condition,
      @RequestHeader(value = "Deokhugam-Request-User-ID") UUID requestUserHeaderId
  );

  @Operation(
      summary = "리뷰 등록",
      description = "새로운 리뷰를 등록합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "리뷰 등록 성공",
          content = @Content(schema = @Schema(implementation = ReviewDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "도서 정보 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "409", description = "이미 작성된 리뷰 존재",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping
  ResponseEntity<ReviewDto> create(@RequestBody @Valid ReviewCreateRequest request);

  @Operation(
      summary = "리뷰 좋아요",
      description = "리뷰에 좋아요를 추가하거나 취소합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "리뷰 좋아요 성공",
          content = @Content(schema = @Schema(implementation = ReviewLikeDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (요청자 ID 누락)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "리뷰 정보 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping("/{reviewId}/like")
  ResponseEntity<ReviewLikeDto> like(@PathVariable UUID reviewId,
      @RequestHeader(value = "Deokhugam-Request-User-ID", required = true) UUID userId);

  @Operation(
      summary = "리뷰 상세 정보 조회",
      description = "리뷰 ID로 상세 정보를 조회합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "리뷰 정보 조회 성공",
          content = @Content(schema = @Schema(implementation = ReviewDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (요청자 ID 누락)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "리뷰 정보 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/{reviewId}")
  ResponseEntity<ReviewDto> findById(@PathVariable UUID reviewId,
      @RequestHeader(value = "Deokhugam-Request-User-ID", required = true) UUID userId);

  @Operation(
      summary = "리뷰 논리 삭제",
      description = "본인이 작성한 리뷰를 논리적으로 삭제합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "리뷰 삭제 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (요청자 ID 누락)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "리뷰 삭제 권한 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "리뷰 정보 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping("/{reviewId}")
  ResponseEntity<Void> delete(
      @PathVariable UUID reviewId,
      @RequestHeader(value = "Deokhugam-Request-User-ID", required = true) UUID userId
  );

  @Operation(
      summary = "리뷰 수정",
      description = "본인이 작성한 리뷰를 수정합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "리뷰 수정 성공",
          content = @Content(schema = @Schema(implementation = ReviewDto.class))),
      @ApiResponse(responseCode = "400", description = "입력값 검증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "리뷰 수정 권한 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "리뷰 정보 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PatchMapping("/{reviewId}")
  ResponseEntity<ReviewDto> update(
      @PathVariable UUID reviewId,
      @RequestHeader(value = "Deokhugam-Request-User-ID", required = true) UUID userId,
      @RequestBody @Valid ReviewUpdateRequest request);

  @Operation(
      summary = "인기 리뷰 목록 조회",
      description = "기간별 인기 리뷰 목록을 조회합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "인기 리뷰 목록 조회 성공",
          content = @Content(schema = @Schema(implementation = CursorPageResponsePopularReviewDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (랭킹 기간 오류, 정렬 방향 오류 등)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/popular")
  ResponseEntity<CursorPageResponsePopularReviewDto> getPopularReviews(
      PopularReviewSearchCondition condition);

  @Operation(
      summary = "리뷰 물리 삭제",
      description = "본인이 작성한 리뷰를 물리적으로 삭제합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "리뷰 삭제 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (요청자 ID 누락)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "리뷰 삭제 권한 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "리뷰 정보 없음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping("/{reviewId}/hard")
  ResponseEntity<Void> hardDelete(
      @PathVariable UUID reviewId,
      @RequestHeader(value = "Deokhugam-Request-User-ID", required = true) UUID userId
  );
}
