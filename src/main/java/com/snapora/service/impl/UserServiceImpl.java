package com.snapora.service.impl;

import com.snapora.exception.ResourceNotFoundException;
import com.snapora.model.dto.request.UpdateProfileRequest;
import com.snapora.model.dto.response.UserResponse;
import com.snapora.model.dto.response.UserSummary;
import com.snapora.model.entity.User;
import com.snapora.repository.FollowRepository;
import com.snapora.repository.PostRepository;
import com.snapora.repository.UserRepository;
import com.snapora.service.UserService;
import com.snapora.util.FileStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private FileStorageUtil fileStorageUtil;

    @Override
    public UserResponse getProfile(String username, String currentUsername) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        User current = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserResponse(user, current);
    }

    @Override
    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserResponse(user, user);
    }

    @Override
    public UserResponse updateProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getIsPrivate() != null) user.setPrivate(request.getIsPrivate());
        userRepository.save(user);
        return mapToUserResponse(user, user);
    }

    @Override
    public UserResponse uploadAvatar(String username, MultipartFile file) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        try {
            if (user.getAvatarUrl() != null) fileStorageUtil.deleteFile(user.getAvatarUrl());
            String url = fileStorageUtil.storeFile(file);
            user.setAvatarUrl(url);
            userRepository.save(user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload avatar", e);
        }
        return mapToUserResponse(user, user);
    }

    @Override
    public Page<UserSummary> searchUsers(String query, String currentUsername, Pageable pageable) {
        User current = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userRepository.searchUsers(query, pageable)
                .map(u -> toUserSummary(u, current));
    }

    private UserResponse mapToUserResponse(User user, User current) {
        boolean isOwn = user.getId().equals(current.getId());
        boolean isFollowing = !isOwn && followRepository.existsByFollowerIdAndFollowingId(current.getId(), user.getId());
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(isOwn ? user.getEmail() : null)
                .fullName(user.getFullName())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .isPrivate(user.isPrivate())
                .postsCount(postRepository.countByUserId(user.getId()))
                .followersCount(followRepository.countByFollowingId(user.getId()))
                .followingCount(followRepository.countByFollowerId(user.getId()))
                .isFollowing(isFollowing)
                .isOwnProfile(isOwn)
                .createdAt(user.getCreatedAt())
                .build();
    }

    public UserSummary toUserSummary(User user, User current) {
        boolean isFollowing = !user.getId().equals(current.getId()) &&
                followRepository.existsByFollowerIdAndFollowingId(current.getId(), user.getId());
        return UserSummary.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .isFollowing(isFollowing)
                .build();
    }
}
