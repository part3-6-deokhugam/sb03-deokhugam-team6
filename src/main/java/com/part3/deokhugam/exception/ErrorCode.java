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

  // 404 Not Found
  ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),

  // 409 Conflict
  DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "이미 존재하는 리소스입니다."),

  // 500 Internal Server Error
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 오류가 발생했습니다."),

  //--- BOOK ---
  BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "책을 찾을 수 없습니다."),
  BOOK_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 책입니다."),
  OCR_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OCR 처리에 실패하였습니다."),
  NAVER_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "네이버 API 요청에 실패하였습니다."),
  NAVER_API_RESPONSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "네이버 API 응답 파싱에 실패하였습니다."),
  IMAGE_DOWNLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "책 표지 이미지 다운로드에 실패하였습니다."),

  //--- COMMENT ---
  COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
  COMMENT_FORBIDDEN(HttpStatus.FORBIDDEN,"댓글에 대한 권한이 없습니다."),

  //--- REVIEW ---
  REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND,"리뷰를 찾을 수 없습니다."),
  REVIEW_METRICS_NOT_FOUND(HttpStatus.NOT_FOUND,"리뷰 Metrics를 찾을 수 없습니다."),
  REVIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "리뷰에 대한 권한이 없습니다."),
  REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT,"이미 리뷰가 존재합니다."),

  //--- USER ---
  USER_NOT_FOUND(HttpStatus.NOT_FOUND,"사용자를 찾을 수 없습니다."),
  USER_FORBIDDEN(HttpStatus.FORBIDDEN,"사용자에 대한 권한이 없습니다."),
  EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
  LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 잘못되었습니다."),

  //--- NOTIFICATION ---
  NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND,"알림을 찾을 수 없습니다."),
  NOTIFICATION_FORBIDDEN(HttpStatus.FORBIDDEN,"알림에 대한 권한이 없습니다."),

  //--- INPUT ERROR ---
  INVALID_ORDER_BY(HttpStatus.BAD_REQUEST, "잘못된 정렬 기준입니다."),
  INVALID_PERIOD(HttpStatus.BAD_REQUEST, "지원하지 않는 기간입니다."),
  EMPTY_IMAGE_FILE(HttpStatus.BAD_REQUEST, "이미지 파일이 비어 있습니다."),
  UNSUPPORTED_IMAGE_FORMAT(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식입니다."),
  INVALID_PAGINATION_CURSOR(HttpStatus.BAD_REQUEST, "잘못된 페이지네이션 커서입니다."),
  INVALID_SORT_DIRECTION(HttpStatus.BAD_REQUEST, "정렬 방향은 'asc' 또는 'desc'여야 합니다.");

  private final HttpStatus status;
  private final String message;

  ErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }

}
