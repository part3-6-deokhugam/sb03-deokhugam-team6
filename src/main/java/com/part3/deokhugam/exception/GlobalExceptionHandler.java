package com.part3.deokhugam.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * BusinessException 처리
   * - ErrorCode에 정의된 상태 코드와 메시지 사용
   */
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
    ErrorCode code = ex.getErrorCode();

    ErrorResponse response = ErrorResponse.builder()
        .status(code.getStatus().value())
        .error(code.name())
        .message(ex.getMessage())   // ErrorCode 메시지 + (detail 포함)
        .build();

    return new ResponseEntity<>(response, code.getStatus());
  }

  /**
   * Bean Validation 예외 처리
   * - @Valid, @Validated로 검증 실패 시 발생하는 예외
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
    // 필드별 오류 메시지를 Map으로 수집
    Map<String, String> errors = new HashMap<>();
    for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
      errors.put(fe.getField(), fe.getDefaultMessage());
    }

    ErrorResponse response = ErrorResponse.builder()
        .status(ErrorCode.INVALID_INPUT_VALUE.getStatus().value())
        .error(ErrorCode.INVALID_INPUT_VALUE.name())
        .message(ErrorCode.INVALID_INPUT_VALUE.getMessage())
        .details(errors)
        .build();

    return new ResponseEntity<>(response, ErrorCode.INVALID_INPUT_VALUE.getStatus());
  }

  /**
   * 그 외 모든 예외 처리 (최상위)
   * - 예측하지 못한 예외 발생 시 500 Internal Server Error 반환
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception ex) {
    ErrorResponse response = ErrorResponse.builder()
        .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value())
        .error(ErrorCode.INTERNAL_SERVER_ERROR.name())
        .message(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
        .build();

    return new ResponseEntity<>(response, ErrorCode.INTERNAL_SERVER_ERROR.getStatus());
  }
}