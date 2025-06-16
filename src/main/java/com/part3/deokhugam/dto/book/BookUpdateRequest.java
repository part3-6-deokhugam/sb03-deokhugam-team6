package com.part3.deokhugam.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record BookUpdateRequest(
        @NotBlank(message = "제목은 반드시 입력되어야 합니다.")
        @Size(max = 255, message = "제목은 255자 이내로 입력해야 합니다.")
        String title,

        @NotBlank(message = "저자는 반드시 입력되어야 합니다.")
        @Size(max = 100, message = "저자는 100자 이내로 입력해야 합니다.")
        String author,

        @NotBlank(message = "설명은 반드시 입력되어야 합니다.")
        @Size(max = 1500, message = "설명은 1500자 이내로 입력해야 합니다.")
        String description,

        @NotBlank(message = "출판사는 반드시 입력되어야 합니다.")
        @Size(max = 100, message = "출판사는 100자 이내로 입력해야 합니다.")
        String publisher,

        @NotNull(message = "출판일은 반드시 입력되어야 합니다.")
        LocalDate publishedDate
) {

}