package com.snapora.controller;

import com.snapora.model.dto.request.UpdateProfileRequest;
import com.snapora.model.dto.response.UserResponse;
import com.snapora.model.dto.response.UserSummary;
import com.snapora.service.UserService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(userService.getCurrentUser(user.getUsername()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(@AuthenticationPrincipal UserDetails user,
                                                       @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(user.getUsername(), request));
    }

    @PostMapping("/me/avatar")
    public ResponseEntity<UserResponse> uploadAvatar(@AuthenticationPrincipal UserDetails user,
                                                      @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.uploadAvatar(user.getUsername(), file));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getProfile(@PathVariable String username,
                                                    @AuthenticationPrincipal UserDetails user) {
        String current = user != null ? user.getUsername() : username;
        return ResponseEntity.ok(userService.getProfile(username, current));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserSummary>> searchUsers(@RequestParam String q,
                                                          @AuthenticationPrincipal UserDetails user,
                                                          Pageable pageable) {
        return ResponseEntity.ok(userService.searchUsers(q, user.getUsername(), pageable));
    }
}
