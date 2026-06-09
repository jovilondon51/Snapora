package com.snapora.service.impl;

import com.snapora.exception.ResourceNotFoundException;
import com.snapora.exception.UnauthorizedException;
import com.snapora.model.dto.request.CreatePostRequest;
import com.snapora.model.dto.response.PostResponse;
import com.snapora.model.dto.response.UserSummary;
import com.snapora.model.entity.*;
import com.snapora.model.enums.NotificationType;
import com.snapora.repository.*;
import com.snapora.service.PostService;
import com.snapora.util.FileStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostLikeRepository postLikeRepository;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private SavedPostRepository savedPostRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private FileStorageUtil fileStorageUtil;

    @Override
    public PostResponse createPost(String username, CreatePostRequest request, MultipartFile image) {
        User user = getUser(username);
        String imageUrl;
        try {
            imageUrl = fileStorageUtil.storeFile(image);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store image", e);
        }
        Post post = Post.builder()
                .user(user)
                .caption(request.getCaption())
                .imageUrl(imageUrl)
                .location(request.getLocation())
                .build();
        return toResponse(postRepository.save(post), user);
    }

    @Override
    public Page<PostResponse> getFeed(String username, Pageable pageable) {
        User user = getUser(username);
        List<Long> followingIds = followRepository.findFollowingIdsByFollowerId(user.getId());
        followingIds.add(user.getId());
        return postRepository.findFeedPosts(followingIds, pageable).map(p -> toResponse(p, user));
    }

    @Override
    public Page<PostResponse> getExplore(String username, Pageable pageable) {
        User user = getUser(username);
        return postRepository.findExplorePosts(pageable).map(p -> toResponse(p, user));
    }

    @Override
    public PostResponse getPost(Long id, String username) {
        User user = getUser(username);
        Post post = getPost(id);
        return toResponse(post, user);
    }

    @Override
    @Transactional
    public void deletePost(Long id, String username) {
        User user = getUser(username);
        Post post = getPost(id);
        if (!post.getUser().getId().equals(user.getId()))
            throw new UnauthorizedException("You can only delete your own posts");
        fileStorageUtil.deleteFile(post.getImageUrl());
        post.setDeleted(true);
        postRepository.save(post);
    }

    @Override
    @Transactional
    public boolean toggleLike(Long id, String username) {
        User user = getUser(username);
        Post post = getPost(id);
        var existing = postLikeRepository.findByPostIdAndUserId(id, user.getId());
        if (existing.isPresent()) {
            postLikeRepository.delete(existing.get());
            return false;
        }
        postLikeRepository.save(PostLike.builder().post(post).user(user).build());
        if (!post.getUser().getId().equals(user.getId())) {
            notificationRepository.save(Notification.builder()
                    .recipient(post.getUser()).actor(user)
                    .type(NotificationType.LIKE).referenceId(post.getId()).build());
        }
        return true;
    }

    @Override
    public Page<PostResponse> getUserPosts(Long userId, String currentUsername, Pageable pageable) {
        User current = getUser(currentUsername);
        return postRepository.findByUserId(userId, pageable).map(p -> toResponse(p, current));
    }

    @Override
    public Page<PostResponse> getSavedPosts(String username, Pageable pageable) {
        User user = getUser(username);
        return savedPostRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(sp -> toResponse(sp.getPost(), user));
    }

    @Override
    @Transactional
    public boolean toggleSave(Long id, String username) {
        User user = getUser(username);
        Post post = getPost(id);
        var existing = savedPostRepository.findByUserIdAndPostId(user.getId(), id);
        if (existing.isPresent()) {
            savedPostRepository.delete(existing.get());
            return false;
        }
        savedPostRepository.save(SavedPost.builder().user(user).post(post).build());
        return true;
    }

    private PostResponse toResponse(Post post, User current) {
        boolean liked = postLikeRepository.existsByPostIdAndUserId(post.getId(), current.getId());
        boolean saved = savedPostRepository.existsByUserIdAndPostId(current.getId(), post.getId());
        return PostResponse.builder()
                .id(post.getId())
                .user(UserSummary.builder()
                        .id(post.getUser().getId())
                        .username(post.getUser().getUsername())
                        .fullName(post.getUser().getFullName())
                        .avatarUrl(post.getUser().getAvatarUrl())
                        .build())
                .caption(post.getCaption())
                .imageUrl(post.getImageUrl())
                .location(post.getLocation())
                .likesCount(postLikeRepository.countByPostId(post.getId()))
                .commentsCount(0)
                .likedByMe(liked)
                .savedByMe(saved)
                .createdAt(post.getCreatedAt())
                .build();
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private Post getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        if (post.isDeleted()) throw new ResourceNotFoundException("Post not found");
        return post;
    }
}
