package com.part3.deokhugam.mapper;

import com.part3.deokhugam.domain.User;
import com.part3.deokhugam.dto.user.UserDto;
import com.part3.deokhugam.dto.user.UserRegisterRequest;
import com.part3.deokhugam.dto.user.UserUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserDto toDto(User user);

  User toEntity(UserRegisterRequest req);

  void updateFromDto(UserUpdateRequest req, @MappingTarget User user);
}