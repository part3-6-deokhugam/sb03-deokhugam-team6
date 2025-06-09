package com.part3.deokhugam.repository;

import com.part3.deokhugam.domain.Comment;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CommentRepositoryCustom {

  List<Comment> findByReviewIdWithCursor(UUID reviewId, String direction, String cursor,
      Instant after, int limit);
}
