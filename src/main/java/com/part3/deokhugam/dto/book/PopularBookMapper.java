package com.part3.deokhugam.dto.book;

import com.part3.deokhugam.domain.PopularBook;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PopularBookMapper {
    PopularBookDto toDto(PopularBook popularBook);
}
