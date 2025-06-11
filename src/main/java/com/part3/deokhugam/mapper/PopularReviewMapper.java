package com.part3.deokhugam.mapper;

import com.part3.deokhugam.domain.PopularReview;
import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.domain.enums.Period;
import java.time.LocalDate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PopularReviewMapper {

  @Mapping(target="id", ignore=true)
  @Mapping(target="rank", ignore=true)
  PopularReview toPopularReview(Review review, Period periodType, LocalDate periodDate, double score, int likeCount, int commentCount);

}
