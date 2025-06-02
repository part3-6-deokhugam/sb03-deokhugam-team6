package com.part3.deokhugam.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private UUID id;
    private UUID reviewId;
    private UUID userId;
    private String userNickname;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
}
