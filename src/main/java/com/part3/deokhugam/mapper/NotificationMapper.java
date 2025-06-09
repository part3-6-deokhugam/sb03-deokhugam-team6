package com.part3.deokhugam.mapper;

import com.part3.deokhugam.domain.Notification;
import com.part3.deokhugam.dto.notification.NotificationDto;
import com.part3.deokhugam.dto.notification.NotificationUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

  // Entity → DTO 변환, review.title → reviewTitle
  @Mapping(target = "reviewTitle", source = "review.book.title")
  NotificationDto toDto(Notification notification);

  // UpdateRequest → Entity (confirmed 필드만 덮어쓰기)
  @Mapping(target = "confirmed", source = "confirmed")
  void updateFromRequest(NotificationUpdateRequest req, @MappingTarget Notification notification);
}