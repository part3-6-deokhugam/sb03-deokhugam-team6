package com.part3.deokhugam.api;

import com.part3.deokhugam.dto.user.UserDto;
import com.part3.deokhugam.dto.user.UserLoginRequest;
import com.part3.deokhugam.dto.user.UserLoginResponse;
import com.part3.deokhugam.dto.user.UserRegisterRequest;
import com.part3.deokhugam.dto.user.UserUpdateRequest;
import com.part3.deokhugam.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "사용자 관리", description = "사용자 관련 API")
@RequestMapping("/api/users")
public interface UserApi {

  @Operation(
      summary = "회원가입",
      description = "새로운 사용자를 가입시킵니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "회원가입 성공",
          content = @Content(schema = @Schema(implementation = UserDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 입력값 (검증 실패)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "409", description = "이메일 중복",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping
  ResponseEntity<UserDto> register(
      @Valid @RequestBody UserRegisterRequest request
  );

  @Operation(
      summary = "로그인",
      description = "이메일과 비밀번호로 로그인합니다. 성공 시 응답 헤더에 사용자 ID를 돌려줍니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "로그인 성공",
          content = @Content(schema = @Schema(implementation = UserLoginResponse.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 입력값 (검증 실패)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "인증 실패 (이메일 또는 비밀번호 불일치)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping("/login")
  ResponseEntity<UserLoginResponse> login(
      @Valid @RequestBody UserLoginRequest request
  );

  @Operation(
      summary = "사용자 정보 조회",
      description = "헤더에 담긴 사용자 ID를 검증한 후, 경로 변수에 해당하는 사용자를 조회합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = UserDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 UUID 형식 등",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "헤더 누락 또는 인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "해당 사용자 없음 또는 삭제됨",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/{userId}")
  ResponseEntity<UserDto> findById(
      @PathVariable UUID userId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID requesterId
  );

  @Operation(
      summary = "닉네임 수정",
      description = "본인의 닉네임을 업데이트합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "수정 성공",
          content = @Content(schema = @Schema(implementation = UserDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 입력값 (검증 실패, UUID 형식 오류 등)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "헤더 누락 또는 인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "권한 없음 (다른 사용자의 정보를 수정 시도)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "해당 사용자 없음 또는 삭제됨",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PatchMapping("/{userId}")
  ResponseEntity<UserDto> updateNickname(
      @PathVariable UUID userId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID requesterId,
      @Valid @RequestBody UserUpdateRequest request
  );

  @Operation(
      summary = "회원 탈퇴 (논리 삭제)",
      description = "헤더에 담긴 본인 ID로 논리 삭제를 수행합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "삭제(탈퇴) 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 UUID 형식 등",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "헤더 누락 또는 인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "권한 없음 (다른 사용자의 탈퇴 시도)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "해당 사용자 없음 또는 이미 삭제됨",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping("/{userId}")
  @ResponseStatus
  void delete(
      @PathVariable UUID userId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID requesterId
  );
}