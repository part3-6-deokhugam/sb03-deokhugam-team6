package com.part3.deokhugam.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateRequest {
  @NotNull
  private UUID bookId;
  @NotNull
  private UUID userId;
  @NotBlank
  private String content;
  @NotNull
  @Min(1)
  @Max(5)
  private Integer rating;
}
