package com.part3.deokhugam.exception;

import java.util.Map;

public class ReviewException extends BusinessException {

  public ReviewException(ErrorCode errorCode) {
    super(errorCode);
  }

  public ReviewException(ErrorCode errorCode, String detail) {
    super(errorCode, detail);
  }

  public ReviewException(ErrorCode errorCode, Map<String, String> details) {
    super(errorCode, details);
  }
}
