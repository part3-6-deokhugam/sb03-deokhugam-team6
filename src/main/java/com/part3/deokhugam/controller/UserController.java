package com.part3.deokhugam.controller;

import com.part3.deokhugam.dto.user.UserDto;
import com.part3.deokhugam.dto.user.UserLoginRequest;
import com.part3.deokhugam.dto.user.UserLoginResponse;
import com.part3.deokhugam.dto.user.UserRegisterRequest;
import com.part3.deokhugam.dto.user.UserUpdateRequest;
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
public class UserController {

  private final UserService userService;

  //회원가입
  @PostMapping
  public ResponseEntity<UserDto> register(@RequestBody @Valid UserRegisterRequest req) {
    UserDto created = userService.register(req);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  //로그인
  @PostMapping("/login")
  public ResponseEntity<UserLoginResponse> login(@RequestBody @Valid UserLoginRequest req) {
    UserLoginResponse resp = userService.login(req);

    // 로그인 성공 시, 응답 헤더에 사용자 ID 포함
    return ResponseEntity.ok()
        .header("Deokhugam-Request-User-ID", resp.getUserId())
        .body(resp);
  }

  //사용자 정보 조회
  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> findById(@PathVariable String userId) {
    UserDto user = userService.findById(userId);
    return ResponseEntity.ok(user);
  }

  //닉네임 수정
  @PatchMapping("/{userId}")
  public ResponseEntity<UserDto> updateNickname(
      @PathVariable String userId,
      @RequestBody @Valid UserUpdateRequest req) {
    UserDto updated = userService.updateNickname(userId, req);
    return ResponseEntity.ok(updated);
  }

  //사용자 논리 삭제 (DELETE /api/users/{userId})
  @DeleteMapping("/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String userId) {
    userService.delete(userId);
  }
}