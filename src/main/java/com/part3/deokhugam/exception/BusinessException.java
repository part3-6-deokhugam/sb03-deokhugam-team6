package com.part3.deokhugam.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
  private final ErrorCode errorCode;
  private final String detail;

  // 기본 생성자: detail 없음
  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.detail = null;
  }

  // 상세 메시지를 포함한 생성자
  public BusinessException(ErrorCode errorCode, String detail) {
    super(errorCode.getMessage() + (detail != null ? " (" + detail + ")" : ""));
    this.errorCode = errorCode;
    this.detail = detail;
  }

}
