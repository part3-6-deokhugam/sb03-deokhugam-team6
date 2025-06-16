package com.part3.deokhugam.exception;

import java.util.Map;

public class BookException extends BusinessException {

  public BookException(ErrorCode errorCode) {
    super(errorCode);
  }

  public BookException(ErrorCode errorCode, String detail) {
    super(errorCode, detail);
  }

  public BookException(ErrorCode errorCode, Map<String, String> details) {
    super(errorCode, details);
  }
}
