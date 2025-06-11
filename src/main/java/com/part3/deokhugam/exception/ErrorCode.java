package com.part3.deokhugam.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  // 400 Bad Request
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력 값입니다."),

  // 401 Unauthorized
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),

  // 403 Forbidden
  FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
  NO_NOTIFICATION_PERMISSION(HttpStatus.FORBIDDEN, "알림 수정 권한 없음"),

  // 404 Not Found
  ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
  NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "알림 정보 없음"),

  // 409 Conflict
  DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "이미 존재하는 리소스입니다."),

  EMAIL_DUPLICATION(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),

  // 500 Internal Server Error
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 오류가 발생했습니다."),

  BOOK_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 책입니다."),
  OCR_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OCR 처리에 실패하였습니다."),
  NAVER_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "네이버 API 요청에 실패하였습니다."),
  NAVER_API_RESPONSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "네이버 API 응답 파싱에 실패하였습니다."),
  IMAGE_DOWNLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "책 표지 이미지 다운로드에 실패하였습니다."),
  BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "책을 찾을 수 없습니다."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.");

  private final HttpStatus status;
  private final String message;

  ErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }

}
