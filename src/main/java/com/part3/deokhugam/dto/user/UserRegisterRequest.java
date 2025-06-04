package com.part3.deokhugam.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterRequest {
  @Email
  @NotBlank
  private String email;

  @NotBlank @Size(min = 8, max = 20)
  private String password;

  @NotBlank @Size(min = 2, max = 20)
  private String nickname;
}