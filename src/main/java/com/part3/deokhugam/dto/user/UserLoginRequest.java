package com.part3.deokhugam.dto.user;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UserLoginRequest {

  @Email
  @NotBlank
  private String email;

  @NotBlank
  private String password;
}