package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
  // 삭제되지 않은(User.deleted = false) 사용자 중 이메일 조회
  Optional<User> findByEmailAndDeletedFalse(String email);
}
