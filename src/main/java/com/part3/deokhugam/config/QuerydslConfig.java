package com.part3.deokhugam.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslConfig {

  @PersistenceContext
  private EntityManager em;

  /**
   * BookRepositoryImpl 생성자 주입을 위해 JPAQueryFactory 빈만 등록.
   * (실제로 QClass가 없어도, 빈이 존재하기만 하면 스프링 컨텍스트가 시작됨)
   */
  @Bean
  public JPAQueryFactory jpaQueryFactory() {
    return new JPAQueryFactory(em);
  }
}