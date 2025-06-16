package com.part3.deokhugam.service;

import com.part3.deokhugam.domain.User;
import com.part3.deokhugam.dto.user.UserDto;
import com.part3.deokhugam.dto.user.UserLoginRequest;
import com.part3.deokhugam.dto.user.UserRegisterRequest;
import com.part3.deokhugam.dto.user.UserUpdateRequest;
import com.part3.deokhugam.exception.ErrorCode;
import com.part3.deokhugam.exception.UserException;
import com.part3.deokhugam.mapper.UserMapper;
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
  private final UserMapper userMapper;

  //회원가입
  @Transactional
  public UserDto register(UserRegisterRequest req) {
    userRepository.findByEmailAndDeletedFalse(req.getEmail())
        .ifPresent(existing -> {
          throw new UserException(
              ErrorCode.EMAIL_ALREADY_EXISTS,
              Map.of("email", req.getEmail())
          );
        });

    // DTO -> Entity 매핑
    User user = userMapper.toEntity(req);
    user.setPassword(passwordEncoder.encode(req.getPassword()));

    try {
      User saved = userRepository.save(user);
      return UserDto.fromEntity(saved);
    } catch (DataIntegrityViolationException e) {
      throw new UserException(
          ErrorCode.EMAIL_ALREADY_EXISTS,
          Map.of("email", req.getEmail())
      );
    }
  }

  //로그인
  @Transactional(readOnly = true)
  public User login(UserLoginRequest req) {
    // 1) 이메일로 사용자 조회
    User user = userRepository.findByEmailAndDeletedFalse(req.getEmail())
        .orElseThrow(() -> new UserException(
            ErrorCode.LOGIN_FAILED,
            Map.of("email", req.getEmail(), "reason", "이메일이 잘못되었습니다.")
        ));

    // 2) 비밀번호 검증
    if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
      throw new UserException(
          ErrorCode.LOGIN_FAILED,
          Map.of("email", req.getEmail(), "reason", "비밀번호가 잘못되었습니다.")
      );
    }

    // 3) 인증 성공한 User 엔티티 반환
    return user;
  }

  //UUID 문자열 검증 & 파싱
  private UUID parseUUID(String id) {
    try {
      return UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw new UserException(
          ErrorCode.INVALID_INPUT_VALUE,
          Map.of("input", id)
      );
    }
  }

  //사용자 조회
  @Transactional(readOnly = true)
  public UserDto findById(String userId) {
    UUID uuid = parseUUID(userId);
    User user = userRepository.findById(uuid)
        .filter(u -> !u.isDeleted())
        .orElseThrow(() -> new UserException(
            ErrorCode.USER_NOT_FOUND,
            Map.of("userId", userId)
        ));

    return userMapper.toDto(user);
  }

  //닉네임 수정
  @Transactional
  public UserDto updateNickname(String userId, UserUpdateRequest req) {
    UUID uuid = parseUUID(userId);
    User user = userRepository.findById(uuid)
        .filter(u -> !u.isDeleted())
        .orElseThrow(() -> new UserException(
            ErrorCode.USER_NOT_FOUND,
            Map.of("userId", userId)
        ));

    userMapper.updateFromDto(req, user);
    User updated = userRepository.save(user);
    return userMapper.toDto(updated);
  }

  //논리삭제
  @Transactional
  public void delete(String userId) {
    UUID uuid = parseUUID(userId);
    User user = userRepository.findById(uuid)
        .filter(u -> !u.isDeleted())
        .orElseThrow(() -> new UserException(
            ErrorCode.USER_NOT_FOUND,
            Map.of("userId", userId)
        ));

    user.delete();
    userRepository.save(user);
  }

  //물리삭제
  @Transactional
  public void hardDelete(String userId) {
    UUID uuid = parseUUID(userId);
    // 존재 여부 체크
    User user = userRepository.findById(uuid)
        .orElseThrow(() -> new UserException(
            ErrorCode.USER_NOT_FOUND,
            Map.of("userId", userId)
        ));
    // 실제 삭제
    userRepository.delete(user);
  }
}
