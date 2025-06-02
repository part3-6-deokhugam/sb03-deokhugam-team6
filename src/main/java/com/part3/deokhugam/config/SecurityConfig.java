package com.part3.deokhugam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // Swagger UI, OpenAPI 스펙은 모두 허용
  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().requestMatchers(
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/webjars/**"
    );
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // CSRF 토큰 비활성화 (API 서버라면 이대로 꺼도 무방)
        .csrf().disable()

        // 나머지 모든 요청은 인증 없이 허용
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()
        )
        // HTTP Basic/FormLogin 비활성화
        .httpBasic().disable()
        .formLogin().disable()
    ;

    return http.build();
  }
}
