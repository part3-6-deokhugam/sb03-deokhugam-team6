package com.part3.deokhugam.exception;

import java.util.Map;

public class NotificationException extends BusinessException {

  public NotificationException(ErrorCode errorCode) {
    super(errorCode);
  }

  public NotificationException(ErrorCode errorCode, String detail) {
    super(errorCode, detail);
  }

  public NotificationException(ErrorCode errorCode, Map<String, String> details) {
    super(errorCode, details);
  }
}