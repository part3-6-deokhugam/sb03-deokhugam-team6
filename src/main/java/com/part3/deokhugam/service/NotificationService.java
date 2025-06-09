package com.part3.deokhugam.service;

import com.part3.deokhugam.domain.Notification;
import com.part3.deokhugam.dto.notification.NotificationDto;
import com.part3.deokhugam.dto.notification.NotificationUpdateRequest;
import com.part3.deokhugam.mapper.NotificationMapper;
import com.part3.deokhugam.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
  private final NotificationRepository repo;
  private final NotificationMapper mapper;

  // 1) 전체 또는 페이지 조회
  @Transactional(readOnly = true)
  public List<NotificationDto> findAllForUser(UUID userId) {
    return repo.findAll().stream()  // 나중에 userId 조건 붙여!
        .map(mapper::toDto)
        .collect(Collectors.toList());
  }

  // 2) 단일 알림 읽음 상태 업데이트
  @Transactional
  public NotificationDto updateConfirmed(UUID id, NotificationUpdateRequest req) {
    Notification n = repo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("알림이 없습니다"));
    n.setConfirmed(req.isConfirmed());
    return mapper.toDto(n);
  }
}