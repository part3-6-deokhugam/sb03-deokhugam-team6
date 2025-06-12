package com.part3.deokhugam.mapper;

import com.part3.deokhugam.domain.PopularBook;
import com.part3.deokhugam.dto.book.PopularBookDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PopularBookMapper {
    PopularBookDto toDto(PopularBook popularBook);
}
