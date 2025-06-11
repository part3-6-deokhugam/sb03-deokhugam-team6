package com.part3.deokhugam.dto.book;

import com.part3.deokhugam.domain.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {
    @Mapping(source = "bookMetrics.reviewCount", target = "reviewCount")
    @Mapping(source = "bookMetrics.averageRating", target = "rating")
    BookDto toDto(Book book);
}
