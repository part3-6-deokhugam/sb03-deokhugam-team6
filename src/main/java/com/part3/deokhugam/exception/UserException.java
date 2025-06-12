package com.part3.deokhugam.exception;

import java.util.Map;

public class UserException extends BusinessException {

  public UserException(ErrorCode errorCode) {
    super(errorCode);
  }

  public UserException(ErrorCode errorCode, String detail) {
    super(errorCode, detail);
  }
  public UserException(ErrorCode errorCode, Map<String, String> details) {
    super(errorCode, details);
  }

}
