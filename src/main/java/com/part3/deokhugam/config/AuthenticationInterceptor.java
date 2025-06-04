package com.part3.deokhugam.config;

import com.part3.deokhugam.domain.User;
import com.part3.deokhugam.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

  private final UserRepository userRepository;

  //인증 검사 예외 경로 목록 (로그인/회원가입/홈/Swagger/OpenAPI 등)
  private static final List<String> EXCLUDE_PATHS = Arrays.asList(
      "/",                       // 홈 (만약 클라이언트 측에서 사용한다면)
      "/api/users",              // 회원가입 (POST /api/users)
      "/api/users/login",        // 로그인 (POST /api/users/login)
      "/swagger-ui.html",        // Swagger UI (선택)
      "/v3/api-docs",            // OpenAPI 문서 (선택)
      "/swagger-ui",             // Swagger redirect
      "/swagger-ui/index.html"   // Swagger UI
  );

  @Override
  public boolean preHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler) throws Exception {

    String uri = request.getRequestURI();

    // 1) 예외 경로라면 인증 없이 통과
    for (String path : EXCLUDE_PATHS) {
      if (uri.startsWith(path)) {
        return true;
      }
    }

    // 2) 나머지 URI는 반드시 헤더 확인
    String userIdHeader = request.getHeader("Deokhugam-Request-User-ID");
    if (userIdHeader == null || userIdHeader.isBlank()) {
      // 401 Unauthorized: 인증 정보 없음
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증 정보가 없습니다.");
      return false;
    }

    // 3) UUID 형식 검증
    UUID userId;
    try {
      userId = UUID.fromString(userIdHeader);
    } catch (IllegalArgumentException e) {
      // 400 Bad Request: 잘못된 UUID 포맷
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "유효하지 않은 사용자 ID 형식입니다.");
      return false;
    }

    // 4) DB에 존재하고 삭제되지 않은 사용자 여부 확인
    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty() || userOpt.get().isDeleted()) {
      // 404 Not Found: 해당 사용자 없음 또는 이미 삭제됨
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "요청한 사용자 정보를 찾을 수 없습니다.");
      return false;
    }

    // 5) 모든 검증 통과 → 컨트롤러 로직으로 진행
    return true;
  }
}