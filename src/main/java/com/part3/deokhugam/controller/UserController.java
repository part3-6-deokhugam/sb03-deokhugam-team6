package com.part3.deokhugam.controller;

import com.part3.deokhugam.api.UserApi;
import com.part3.deokhugam.dto.user.UserDto;
import com.part3.deokhugam.dto.user.UserLoginRequest;
import com.part3.deokhugam.dto.user.UserLoginResponse;
import com.part3.deokhugam.dto.user.UserRegisterRequest;
import com.part3.deokhugam.dto.user.UserUpdateRequest;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController implements UserApi {

  private final UserService userService;

  /**
   * 회원가입
   */
  @Override
  public ResponseEntity<UserDto> register(@Valid @RequestBody UserRegisterRequest request) {
    UserDto created = userService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  /**
   * 로그인
   */
  @Override
  public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
    UserLoginResponse resp = userService.login(request);
    return ResponseEntity.ok()
        .header("Deokhugam-Request-User-ID", resp.getUserId())
        .body(resp);
  }

  /**
   * 사용자 정보 조회
   */
  @Override
  public ResponseEntity<UserDto> findById(
      @PathVariable UUID userId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID requesterId
  ) {
    // 본인 조회가 아니면 403
    if (!requesterId.equals(userId)) {
      throw new BusinessException(ErrorCode.FORBIDDEN, "다른 사용자의 정보를 조회할 수 없습니다.");
    }

    UserDto user = userService.findById(userId.toString());
    return ResponseEntity.ok(user);
  }

  /**
   * 닉네임 수정
   */
  @Override
  public ResponseEntity<UserDto> updateNickname(
      @PathVariable UUID userId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID requesterId,
      @Valid @RequestBody UserUpdateRequest request
  ) {
    // 본인이 아니면 403
    if (!requesterId.equals(userId)) {
      throw new BusinessException(ErrorCode.FORBIDDEN, "다른 사용자의 닉네임을 수정할 수 없습니다.");
    }

    UserDto updated = userService.updateNickname(userId.toString(), request);
    return ResponseEntity.ok(updated);
  }

  /**
   * 회원 탈퇴 (논리 삭제)
   */
  @Override
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
      @PathVariable UUID userId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID requesterId
  ) {
    // 본인이 아니면 403
    if (!requesterId.equals(userId)) {
      throw new BusinessException(ErrorCode.FORBIDDEN, "다른 사용자의 탈퇴를 수행할 수 없습니다.");
    }

    userService.delete(userId.toString());
  }
}