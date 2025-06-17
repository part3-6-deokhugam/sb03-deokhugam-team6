package com.part3.deokhugam.mapper;

import com.part3.deokhugam.domain.PopularBook;
import com.part3.deokhugam.dto.book.PopularBookDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PopularBookMapper {
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "title")
    @Mapping(source = "book.author", target = "author")
    @Mapping(source = "book.thumbnailUrl", target = "thumbnailUrl")
    @Mapping(source = "book.bookMetrics.averageRating", target = "rating")
    PopularBookDto toDto(PopularBook popularBook);
}
