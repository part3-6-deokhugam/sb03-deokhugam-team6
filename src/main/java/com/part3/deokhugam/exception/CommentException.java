package com.part3.deokhugam.exception;

import java.util.Map;

public class CommentException extends BusinessException {

  public CommentException(ErrorCode errorCode) {
    super(errorCode);
  }

  public CommentException(ErrorCode errorCode, String detail) {
    super(errorCode, detail);
  }

  public CommentException(ErrorCode errorCode, Map<String, String> details) {
    super(errorCode, details);
  }
}
