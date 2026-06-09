package com.snapora.service;

import com.snapora.model.dto.request.CreatePostRequest;
import com.snapora.model.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {
    PostResponse createPost(String username, CreatePostRequest request, MultipartFile image);
    Page<PostResponse> getFeed(String username, Pageable pageable);
    Page<PostResponse> getExplore(String username, Pageable pageable);
    PostResponse getPost(Long id, String username);
    void deletePost(Long id, String username);
    boolean toggleLike(Long id, String username);
    Page<PostResponse> getUserPosts(Long userId, String currentUsername, Pageable pageable);
    Page<PostResponse> getSavedPosts(String username, Pageable pageable);
    boolean toggleSave(Long id, String username);
}
