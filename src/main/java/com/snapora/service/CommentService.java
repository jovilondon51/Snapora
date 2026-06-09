package com.snapora.service;

import com.snapora.model.dto.request.CommentRequest;
import com.snapora.model.dto.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    CommentResponse addComment(Long postId, String username, CommentRequest request);
    Page<CommentResponse> getComments(Long postId, Pageable pageable);
}
