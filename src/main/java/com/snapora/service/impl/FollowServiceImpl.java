package com.snapora.service.impl;

import com.snapora.exception.ResourceNotFoundException;
import com.snapora.model.dto.response.UserSummary;
import com.snapora.model.entity.Follow;
import com.snapora.model.entity.Notification;
import com.snapora.model.entity.User;
import com.snapora.model.enums.NotificationType;
import com.snapora.repository.FollowRepository;
import com.snapora.repository.NotificationRepository;
import com.snapora.repository.UserRepository;
import com.snapora.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    @Transactional
    public boolean toggleFollow(Long targetUserId, String currentUsername) {
        User current = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (current.getId().equals(targetUserId))
            throw new IllegalArgumentException("Cannot follow yourself");
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var existing = followRepository.findByFollowerIdAndFollowingId(current.getId(), targetUserId);
        if (existing.isPresent()) {
            followRepository.delete(existing.get());
            return false;
        }
        followRepository.save(Follow.builder().follower(current).following(target).build());
        notificationRepository.save(Notification.builder()
                .recipient(target).actor(current)
                .type(NotificationType.FOLLOW).build());
        return true;
    }

    @Override
    public Page<UserSummary> getFollowers(Long userId, String currentUsername, Pageable pageable) {
        User current = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return followRepository.findByFollowingId(userId, pageable)
                .map(f -> toSummary(f.getFollower(), current));
    }

    @Override
    public Page<UserSummary> getFollowing(Long userId, String currentUsername, Pageable pageable) {
        User current = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return followRepository.findByFollowerId(userId, pageable)
                .map(f -> toSummary(f.getFollowing(), current));
    }

    private UserSummary toSummary(User user, User current) {
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
