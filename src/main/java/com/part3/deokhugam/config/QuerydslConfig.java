package com.part3.deokhugam.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslConfig {

  @PersistenceContext

  @Bean
  public JPAQueryFactory jpaQueryFactory() {
  }
}