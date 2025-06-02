package com.part3.deokhugam.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
@Builder
public class ErrorResponse {
  private int status;               // HTTP 상태 코드 (e.g., 400, 404, 409 등)
  private String error;             // ErrorCode 이름 (e.g., INVALID_INPUT_VALUE)
  private String message;           // 기본 메시지 (ErrorCode.message)
  private Map<String, String> details;  // (선택) 필드별 검증 오류 등 추가 정보

  // @Builder를 사용하면 필요한 필드만 선택해서 채울 수 있어요.
}