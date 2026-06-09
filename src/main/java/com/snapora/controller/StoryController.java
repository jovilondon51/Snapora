package com.snapora.controller;

import com.snapora.model.dto.response.StoryResponse;
import com.snapora.service.StoryService;
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

import java.util.List;

@RestController
@RequestMapping("/api/stories")
public class StoryController {

    @Autowired
    private StoryService storyService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<StoryResponse> createStory(@AuthenticationPrincipal UserDetails user,
                                                      @RequestParam("media") MultipartFile media) {
        return ResponseEntity.ok(storyService.createStory(user.getUsername(), media));
    }

    @GetMapping("/feed")
    public ResponseEntity<List<StoryResponse>> getFeedStories(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(storyService.getFeedStories(user.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryResponse> viewStory(@PathVariable Long id,
                                                    @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(storyService.viewStory(id, user.getUsername()));
    }
}
