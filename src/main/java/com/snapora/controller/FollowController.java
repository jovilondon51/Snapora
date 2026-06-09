package com.snapora.controller;

import com.snapora.model.dto.response.UserSummary;
import com.snapora.service.FollowService;
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

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class FollowController {

    @Autowired
    private FollowService followService;

    @PostMapping("/{id}/follow")
    public ResponseEntity<Map<String, Boolean>> toggleFollow(@PathVariable Long id,
                                                              @AuthenticationPrincipal UserDetails user) {
        boolean following = followService.toggleFollow(id, user.getUsername());
        return ResponseEntity.ok(Map.of("following", following));
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<Page<UserSummary>> getFollowers(@PathVariable Long id,
                                                           @AuthenticationPrincipal UserDetails user,
                                                           Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowers(id, user.getUsername(), pageable));
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<Page<UserSummary>> getFollowing(@PathVariable Long id,
                                                           @AuthenticationPrincipal UserDetails user,
                                                           Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowing(id, user.getUsername(), pageable));
    }
}
