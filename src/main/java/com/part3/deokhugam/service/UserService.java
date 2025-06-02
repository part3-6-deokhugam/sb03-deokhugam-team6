package com.part3.deokhugam.service;

import com.part3.deokhugam.domain.User;
import com.part3.deokhugam.dto.user.UserDto;
import com.part3.deokhugam.dto.user.UserRegisterRequest;
import com.part3.deokhugam.dto.user.UserUpdateRequest;
import com.part3.deokhugam.exception.BusinessException;
import com.part3.deokhugam.exception.ErrorCode;
import com.part3.deokhugam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  //회원가입
  @Transactional
  public UserDto register(UserRegisterRequest req) {
    userRepository.findByEmailAndDeletedFalse(req.getEmail())
        .ifPresent(u -> {
          throw new BusinessException(
              ErrorCode.DUPLICATE_RESOURCE,
              "email: " + req.getEmail()
          );
        });

    //추후 BCrypt 등으로 비밀번호 암호화 예정
    User user = User.builder()
        .email(req.getEmail())
        .password(req.getPassword())
        .nickname(req.getNickname())
        .build();

    User saved = userRepository.save(user);
    return UserDto.fromEntity(saved);
  }

  //사용자 조회
  @Transactional(readOnly = true)
  public UserDto findById(UUID userId) {
    User user = userRepository.findById(userId)
        .filter(u -> !u.isDeleted())
        .orElseThrow(() ->
            new BusinessException(
                ErrorCode.ENTITY_NOT_FOUND,
                "userId: " + userId
            )
        );
    return UserDto.fromEntity(user);
  }

  //닉네임 수정
  @Transactional
  public UserDto updateNickname(UUID userId, UserUpdateRequest req) {
    User user = userRepository.findById(userId)
        .filter(u -> !u.isDeleted())
        .orElseThrow(() ->
            new BusinessException(
                ErrorCode.ENTITY_NOT_FOUND,
                "userId: " + userId
            )
        );
    user.updateNickname(req.getNickname());
    return UserDto.fromEntity(user);
  }

  //논리삭제
  @Transactional
  public void deleteUser(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() ->
            new BusinessException(
                ErrorCode.ENTITY_NOT_FOUND,
                "userId: " + userId
            )
        );
    user.delete();
  }

  /**
   * (추후 구현) 로그인 로직 인터페이스
   * public UserDto login(UserLoginRequest req) { … }
   */
}
