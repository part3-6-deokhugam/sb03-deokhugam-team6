package com.part3.deokhugam.dto.user;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UserDto {
  private UUID id;
  private String email;
  private String nickname;
  private Instant createdAt;
  private Instant updatedAt; // Swagger 스펙에는 없어도, 확장 용도로 허용

  public static UserDto fromEntity(com.part3.deokhugam.domain.User user) {
    return UserDto.builder()
        .id(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .build();
  }
}