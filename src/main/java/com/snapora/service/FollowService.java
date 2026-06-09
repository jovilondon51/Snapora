package com.snapora.service;

import com.snapora.model.dto.response.UserSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowService {
    boolean toggleFollow(Long targetUserId, String currentUsername);
    Page<UserSummary> getFollowers(Long userId, String currentUsername, Pageable pageable);
    Page<UserSummary> getFollowing(Long userId, String currentUsername, Pageable pageable);
}
