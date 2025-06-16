package com.part3.deokhugam.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  private final AuthenticationInterceptor authInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(authInterceptor)
        .addPathPatterns("/**")   // 모든 경로에 인터셉터 적용
        .order(1);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    // 필요한 경우 CORS 설정 (예: 프론트엔드 호출 허용)
    registry.addMapping("/**")
        .allowedOrigins("*")
        .allowedHeaders("*")
        .allowedMethods("*");
  }
}