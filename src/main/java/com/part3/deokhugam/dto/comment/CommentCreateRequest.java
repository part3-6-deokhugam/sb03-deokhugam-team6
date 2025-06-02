package com.part3.deokhugam.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {
        @NotNull
        private UUID reviewId;
        @NotNull
        private UUID userId;
        @NotBlank
        private String content;
}
