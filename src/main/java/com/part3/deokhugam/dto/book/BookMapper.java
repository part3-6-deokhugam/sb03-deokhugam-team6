package com.part3.deokhugam.dto.book;

import com.part3.deokhugam.domain.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookDto toDto(Book book);
}
