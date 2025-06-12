package com.part3.deokhugam.dto.review;

import com.part3.deokhugam.domain.enums.Direction;
import com.part3.deokhugam.domain.enums.Period;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PopularReviewSearchCondition {

  private Period period = Period.DAILY;
  private Direction direction = Direction.ASC;
  private String cursor;
  private Instant after;
  private int limit = 50;
}
