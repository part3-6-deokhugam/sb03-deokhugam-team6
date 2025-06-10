package com.part3.deokhugam.dto.user;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResponse {
  private String id;
  private String email;
  private String nickname;
  private Instant createdAt;
}