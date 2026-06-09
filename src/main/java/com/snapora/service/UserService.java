package com.snapora.service;

import com.snapora.model.dto.request.UpdateProfileRequest;
import com.snapora.model.dto.response.UserResponse;
import com.snapora.model.dto.response.UserSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserResponse getProfile(String username, String currentUsername);
    UserResponse getCurrentUser(String username);
    UserResponse updateProfile(String username, UpdateProfileRequest request);
    UserResponse uploadAvatar(String username, MultipartFile file);
    Page<UserSummary> searchUsers(String query, String currentUsername, Pageable pageable);
}
