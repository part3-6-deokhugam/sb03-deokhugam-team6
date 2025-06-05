package com.part3.deokhugam.exception;

public class NaverBookException extends BusinessException {
  public NaverBookException(ErrorCode errorCode) {
    super(errorCode);
  }

  public NaverBookException(ErrorCode errorCode, String detail) {
    super(errorCode, detail);
  }
}
