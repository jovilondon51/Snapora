package com.snapora.controller;

import com.snapora.model.dto.request.CreatePostRequest;
import com.snapora.model.dto.response.PostResponse;
import com.snapora.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<PostResponse> createPost(@AuthenticationPrincipal UserDetails user,
                                                    @RequestPart("data") CreatePostRequest request,
                                                    @RequestPart("image") MultipartFile image) {
        return ResponseEntity.ok(postService.createPost(user.getUsername(), request, image));
    }

    @GetMapping("/feed")
    public ResponseEntity<Page<PostResponse>> getFeed(@AuthenticationPrincipal UserDetails user, Pageable pageable) {
        return ResponseEntity.ok(postService.getFeed(user.getUsername(), pageable));
    }

    @GetMapping("/explore")
    public ResponseEntity<Page<PostResponse>> getExplore(@AuthenticationPrincipal UserDetails user, Pageable pageable) {
        return ResponseEntity.ok(postService.getExplore(user.getUsername(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(postService.getPost(id, user.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        postService.deletePost(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Boolean>> toggleLike(@PathVariable Long id,
                                                            @AuthenticationPrincipal UserDetails user) {
        boolean liked = postService.toggleLike(id, user.getUsername());
        return ResponseEntity.ok(Map.of("liked", liked));
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<Map<String, Boolean>> toggleSave(@PathVariable Long id,
                                                            @AuthenticationPrincipal UserDetails user) {
        boolean saved = postService.toggleSave(id, user.getUsername());
        return ResponseEntity.ok(Map.of("saved", saved));
    }
}
