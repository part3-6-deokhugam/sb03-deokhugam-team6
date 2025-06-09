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

  @Query("""
    SELECT n 
      FROM Notification n 
     WHERE n.user.id = :userId 
       AND (:cursor IS NULL OR n.createdAt < :cursor)
     ORDER BY n.createdAt DESC
    """)
  List<Notification> findByUserIdAndCreatedAtBefore(
      @Param("userId") UUID userId,
      @Param("cursor") Instant cursor,
      Pageable pageable);

  @Query("""
    SELECT n
      FROM Notification n
     WHERE n.user.id = :userId
       AND n.createdAt > :after
     ORDER BY n.createdAt ASC
  """)
  List<Notification> findByUserIdAndCreatedAtAfter(
      @Param("userId") UUID userId,
      @Param("after") Instant after,
      Pageable pageable);

  long countByUserId(UUID userId);
}