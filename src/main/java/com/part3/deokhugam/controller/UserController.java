package com.part3.deokhugam.controller;

import com.part3.deokhugam.dto.user.UserDto;
import com.part3.deokhugam.dto.user.UserRegisterRequest;
import com.part3.deokhugam.dto.user.UserUpdateRequest;
import com.part3.deokhugam.service.UserService;
import lombok.RequiredArgsConstructor;
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
  public ResponseEntity<UserDto> register(
      @RequestBody @Validated UserRegisterRequest req) {
    UserDto dto = userService.register(req);
    return ResponseEntity.ok(dto);
  }

  //사용자 정보 조회
  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> getUser(
      @PathVariable UUID userId) {
    UserDto dto = userService.findById(userId);
    return ResponseEntity.ok(dto);
  }

  //닉네임 수정
  @PatchMapping("/{userId}")
  public ResponseEntity<UserDto> updateNickname(
      @PathVariable UUID userId,
      @RequestBody @Validated UserUpdateRequest req) {
    UserDto dto = userService.updateNickname(userId, req);
    return ResponseEntity.ok(dto);
  }

  //사용자 논리 삭제 (DELETE /api/users/{userId})
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(
      @PathVariable UUID userId) {
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build();
  }

  /**
   * (추후) 로그인 API (POST /api/users/login)
   * @PostMapping("/login")
   * public ResponseEntity<UserDto> login(@RequestBody @Validated UserLoginRequest req) { … }
   */
}