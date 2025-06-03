package com.part3.deokhugam.service;

import com.part3.deokhugam.domain.Comment;
import com.part3.deokhugam.domain.Review;
import com.part3.deokhugam.domain.User;
import com.part3.deokhugam.dto.comment.CommentCreateRequest;
import com.part3.deokhugam.dto.comment.CommentDto;
import com.part3.deokhugam.dto.comment.CommentUpdateRequest;
import com.part3.deokhugam.mapper.CommentMapper;
import com.part3.deokhugam.repository.CommentRepository;
import com.part3.deokhugam.repository.ReviewRepository;
import com.part3.deokhugam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentDto create(CommentCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User Not Found"));
        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new RuntimeException("Review Not Found"));

        Comment comment = commentMapper.toEntity(request, user, review);
        Comment savedComment = commentRepository.save(comment);

        return commentMapper.toDto(savedComment);
    }
    @Transactional
    public CommentDto update(UUID commentId, UUID userId, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new RuntimeException("Comment Not Found"));
        if(!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("댓글 작성자가 아닙니다");
        }
        comment.update(request.getContent());
        return commentMapper.toDto(comment);

    }
}
