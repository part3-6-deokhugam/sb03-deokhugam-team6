package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.Notification;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

  // 1) 커서 없이: 그냥 userId 로 최신순 조회
  @Query("""
    SELECT n
      FROM Notification n
     WHERE n.user.id = :userId
     ORDER BY n.createdAt DESC
  """)
  List<Notification> findByUserIdOrderByCreatedAtDesc(
      @Param("userId") UUID userId,
      Pageable pageable);

  // 2) 커서 있을 때: createdAt < cursor 조건
  @Query("""
    SELECT n
      FROM Notification n
     WHERE n.user.id = :userId
       AND n.createdAt < :cursor
     ORDER BY n.createdAt DESC
  """)
  List<Notification> findByUserIdAndCreatedAtBeforeOrderByCreatedAtDesc(
      @Param("userId") UUID userId,
      @Param("cursor") Instant cursor,
      Pageable pageable);

  // 3) after용: cursor 대신 after 로 오름차순 정렬해서 조회
  @Query("""
    SELECT n
      FROM Notification n
     WHERE n.user.id = :userId
       AND n.createdAt > :after
     ORDER BY n.createdAt ASC
  """)
  List<Notification> findByUserIdAndCreatedAtAfterOrderByCreatedAtAsc(
      @Param("userId") UUID userId,
      @Param("after") Instant after,
      Pageable pageable);

  long countByUserId(UUID userId);

  // (optional) 아직 읽지 않은 알림 조회용
  List<Notification> findByUserIdAndConfirmedFalse(UUID userId);

  // 일주일 보다 오래된 확인된 알림은 자동으로 삭제
  void deleteByConfirmedTrueAndUpdatedAtBefore(Instant cutoff);
}