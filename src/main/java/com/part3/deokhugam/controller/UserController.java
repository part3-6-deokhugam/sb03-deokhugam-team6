package com.part3.deokhugam.controller;

import com.part3.deokhugam.api.UserApi;
import com.part3.deokhugam.dto.user.*;
import com.part3.deokhugam.exception.BusinessException;
import com.part3.deokhugam.exception.ErrorCode;
import com.part3.deokhugam.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
public class UserController implements UserApi {

  private final UserService userService;

  //회원가입 (POST /api/users)
  @Override
  @PostMapping("/api/users")
  public ResponseEntity<UserDto> register(@Valid @RequestBody UserRegisterRequest request) {
    UserDto created = userService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  //로그인 (POST /api/users/login)
  @Override
  @PostMapping("/api/users/login")
  public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
    UserLoginResponse resp = userService.login(request);

    // 로그인 성공 시, 응답 헤더에 사용자 ID 포함
    return ResponseEntity.ok()
        .header("Deokhugam-Request-User-ID", resp.getUserId())
        .body(resp);
  }

  //사용자 정보 조회 (GET /api/users/{userId})
  @Override
  @GetMapping("/api/users/{userId}")
  public ResponseEntity<UserDto> findById(
      @PathVariable UUID userId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID requesterId
  ) {
    // 권한 검사: 요청자 ID와 조회 대상 ID가 같아야 함
    if (!userId.equals(requesterId)) {
      throw new BusinessException(ErrorCode.FORBIDDEN, "조회 권한이 없습니다.");
    }

    UserDto user = userService.findById(userId.toString());
    return ResponseEntity.ok(user);
  }

  //닉네임 수정 (PATCH /api/users/{userId})
  @Override
  @PatchMapping("/api/users/{userId}")
  public ResponseEntity<UserDto> updateNickname(
      @PathVariable UUID userId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID requesterId,
      @Valid @RequestBody UserUpdateRequest request
  ) {
    // 권한 검사: 요청자 ID와 수정 대상 ID가 같아야 함
    if (!userId.equals(requesterId)) {
      throw new BusinessException(ErrorCode.FORBIDDEN, "수정 권한이 없습니다.");
    }

    UserDto updated = userService.updateNickname(userId.toString(), request);
    return ResponseEntity.ok(updated);
  }

  //사용자 논리 삭제 (DELETE /api/users/{userId})
  @Override
  @DeleteMapping("/api/users/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> deleteLogically(
      @PathVariable UUID userId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID requesterId
  ) {
    // 권한 검사: 요청자 ID와 삭제 대상 ID가 같아야 함
    if (!userId.equals(requesterId)) {
      throw new BusinessException(ErrorCode.FORBIDDEN, "삭제 권한이 없습니다.");
    }

    userService.delete(userId.toString());
    return ResponseEntity.noContent().build();
  }
}