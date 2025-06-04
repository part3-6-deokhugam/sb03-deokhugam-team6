package com.part3.deokhugam.exception;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
  private final ErrorCode errorCode;
  // 기존의 단일 String 대신, 키-값 쌍으로 여러 필드를 담을 수 있는 Map
  private final Map<String, String> details;

  // (1) 디테일 없을 때
  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.details = null;
  }

  // (2) 기존 호환성: 한 개의 String 디테일만 넘길 때
  public BusinessException(ErrorCode errorCode, String detail) {
    super(errorCode.getMessage() + (detail != null ? " (" + detail + ")" : ""));
    this.errorCode = errorCode;
    // 단일 String을 “detail”이라는 키 하나로 Map에 담아서 저장
    this.details = Map.of("detail", detail);
  }

  // (3) 새로 추가: 필드명→값 쌍 형태의 Map을 넘길 때 사용하는 생성자
  public BusinessException(ErrorCode errorCode, Map<String, String> details) {
    // Exception의 기본 메시지는 ErrorCode에 정의된 message
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.details = details;
  }
}