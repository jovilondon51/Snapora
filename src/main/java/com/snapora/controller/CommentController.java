package com.snapora.controller;

import com.snapora.model.dto.request.CommentRequest;
import com.snapora.model.dto.response.CommentResponse;
import com.snapora.service.CommentService;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long postId,
                                                       @AuthenticationPrincipal UserDetails user,
                                                       @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.addComment(postId, user.getUsername(), request));
    }

    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getComments(@PathVariable Long postId, Pageable pageable) {
        return ResponseEntity.ok(commentService.getComments(postId, pageable));
    }
}
