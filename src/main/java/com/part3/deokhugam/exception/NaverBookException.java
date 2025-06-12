package com.part3.deokhugam.exception;

import java.util.Map;

public class NaverBookException extends BusinessException {

  public NaverBookException(ErrorCode errorCode) {
    super(errorCode);
  }

  public NaverBookException(ErrorCode errorCode, String detail) {
    super(errorCode, detail);
  }

  public NaverBookException(ErrorCode errorCode, Map<String, String> details) {
    super(errorCode, details);
  }
}
