package com.part3.deokhugam.service;

import com.part3.deokhugam.domain.User;
import com.part3.deokhugam.dto.user.UserDto;
import com.part3.deokhugam.dto.user.UserLoginRequest;
import com.part3.deokhugam.dto.user.UserLoginResponse;
import com.part3.deokhugam.dto.user.UserRegisterRequest;
import com.part3.deokhugam.dto.user.UserUpdateRequest;
import com.part3.deokhugam.exception.BusinessException;
import com.part3.deokhugam.exception.ErrorCode;
import com.part3.deokhugam.repository.UserRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  //회원가입
  @Transactional
  public UserDto register(UserRegisterRequest req) {
    userRepository.findByEmailAndDeletedFalse(req.getEmail())
        .ifPresent(existing -> {
          throw new BusinessException(
              ErrorCode.EMAIL_DUPLICATION,
              Map.of("email", req.getEmail())
          );
        });

    // DTO -> Entity 매핑
    User user = User.builder()
        .email(req.getEmail())
        .password(passwordEncoder.encode(req.getPassword()))
        .nickname(req.getNickname())
        .build();

    try {
      User saved = userRepository.save(user);
      return UserDto.fromEntity(saved);
    } catch (DataIntegrityViolationException e) {
      throw new BusinessException(
          ErrorCode.EMAIL_DUPLICATION,
          Map.of("email", req.getEmail())
      );
    }
  }

  //로그인
  @Transactional(readOnly = true)
  public UserLoginResponse login(UserLoginRequest req) {
    // 이메일로 사용자 조회 (삭제되지 않은 상태)
    User user = userRepository.findByEmailAndDeletedFalse(req.getEmail())
        .orElseThrow(() -> new BusinessException(
            ErrorCode.UNAUTHORIZED,
            "이메일 또는 비밀번호가 잘못되었습니다."
        ));

    // 비밀번호 검증
    if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
      throw new BusinessException(
          ErrorCode.UNAUTHORIZED,
          "이메일 또는 비밀번호가 잘못되었습니다."
      );
    }

    // 로그인 성공 → 사용자 ID 반환
    return new UserLoginResponse(user.getId().toString());
  }

  //UUID 문자열 검증 & 파싱
  private UUID parseUUID(String id) {
    try {
      return UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw new BusinessException(
          ErrorCode.INVALID_INPUT_VALUE,
          "유효하지 않은 UUID 형식: " + id
      );
    }
  }

  //사용자 조회
  @Transactional(readOnly = true)
  public UserDto findById(String userId) {
    UUID uuid = parseUUID(userId);
    User user = userRepository.findById(uuid)
        .filter(u -> !u.isDeleted())
        .orElseThrow(() -> new BusinessException(
            ErrorCode.ENTITY_NOT_FOUND,
            "userId: " + userId
        ));

    return UserDto.fromEntity(user);
  }

  //닉네임 수정
  @Transactional
  public UserDto updateNickname(String userId, UserUpdateRequest req) {
    UUID uuid = parseUUID(userId);
    User user = userRepository.findById(uuid)
        .filter(u -> !u.isDeleted())
        .orElseThrow(() -> new BusinessException(
            ErrorCode.ENTITY_NOT_FOUND,
            "사용자를 찾을 수 없습니다: " + userId
        ));

    user.updateNickname(req.getNickname());
    User updated = userRepository.save(user);
    return UserDto.fromEntity(updated);
  }

  //논리삭제
  @Transactional
  public void delete(String userId) {
    UUID uuid = parseUUID(userId);
    User user = userRepository.findById(uuid)
        .filter(u -> !u.isDeleted())
        .orElseThrow(() -> new BusinessException(
            ErrorCode.ENTITY_NOT_FOUND,
            "사용자를 찾을 수 없습니다: " + userId
        ));

    user.delete();
    userRepository.save(user);
  }
}
