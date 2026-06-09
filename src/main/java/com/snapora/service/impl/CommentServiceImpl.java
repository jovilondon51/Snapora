package com.snapora.service.impl;

import com.snapora.exception.ResourceNotFoundException;
import com.snapora.exception.UnauthorizedException;
import com.snapora.model.dto.request.CommentRequest;
import com.snapora.model.dto.response.CommentResponse;
import com.snapora.model.dto.response.UserSummary;
import com.snapora.model.entity.*;
import com.snapora.model.enums.NotificationType;
import com.snapora.repository.*;
import com.snapora.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public CommentResponse addComment(Long postId, String username, CommentRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
        }

        Comment comment = Comment.builder()
                .post(post).user(user).content(request.getContent()).parent(parent).build();
        comment = commentRepository.save(comment);

        if (!post.getUser().getId().equals(user.getId())) {
            notificationRepository.save(Notification.builder()
                    .recipient(post.getUser()).actor(user)
                    .type(NotificationType.COMMENT).referenceId(post.getId()).build());
        }
        return toResponse(comment);
    }

    @Override
    public Page<CommentResponse> getComments(Long postId, Pageable pageable) {
        return commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtAsc(postId, pageable)
                .map(this::toResponse);
    }

    private CommentResponse toResponse(Comment c) {
        List<CommentResponse> replies = c.getReplies().stream()
                .map(this::toResponse).collect(Collectors.toList());
        return CommentResponse.builder()
                .id(c.getId())
                .user(UserSummary.builder()
                        .id(c.getUser().getId())
                        .username(c.getUser().getUsername())
                        .fullName(c.getUser().getFullName())
                        .avatarUrl(c.getUser().getAvatarUrl())
                        .build())
                .content(c.getContent())
                .parentId(c.getParent() != null ? c.getParent().getId() : null)
                .replies(replies)
                .createdAt(c.getCreatedAt())
                .build();
    }
}
