package com.snapora.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snapora.exception.ResourceNotFoundException;
import com.snapora.model.dto.response.StoryResponse;
import com.snapora.model.dto.response.UserSummary;
import com.snapora.model.entity.Story;
import com.snapora.model.entity.User;
import com.snapora.model.enums.MediaType;
import com.snapora.repository.FollowRepository;
import com.snapora.repository.StoryRepository;
import com.snapora.repository.UserRepository;
import com.snapora.service.StoryService;
import com.snapora.util.FileStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoryServiceImpl implements StoryService {

    @Autowired
    private StoryRepository storyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private FileStorageUtil fileStorageUtil;
    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public StoryResponse createStory(String username, MultipartFile media) {
        User user = getUser(username);
        String url;
        try {
            url = fileStorageUtil.storeFile(media);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store media", e);
        }
        MediaType type = media.getContentType() != null && media.getContentType().startsWith("video")
                ? MediaType.VIDEO : MediaType.IMAGE;
        Story story = Story.builder()
                .user(user).mediaUrl(url).mediaType(type)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();
        return toResponse(storyRepository.save(story), user);
    }

    @Override
    public List<StoryResponse> getFeedStories(String username) {
        User user = getUser(username);
        List<Long> ids = followRepository.findFollowingIdsByFollowerId(user.getId());
        ids.add(user.getId());
        return storyRepository.findActiveStoriesForUsers(ids, LocalDateTime.now())
                .stream().map(s -> toResponse(s, user)).collect(Collectors.toList());
    }

    @Override
    public StoryResponse viewStory(Long storyId, String username) {
        User user = getUser(username);
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found"));
        try {
            List<Long> viewers = objectMapper.readValue(story.getViewedBy(), new TypeReference<>() {});
            if (!viewers.contains(user.getId())) {
                viewers.add(user.getId());
                story.setViewedBy(objectMapper.writeValueAsString(viewers));
                storyRepository.save(story);
            }
        } catch (IOException ignored) {}
        return toResponse(story, user);
    }

    private StoryResponse toResponse(Story s, User current) {
        boolean viewed = false;
        try {
            List<Long> viewers = objectMapper.readValue(s.getViewedBy(), new TypeReference<>() {});
            viewed = viewers.contains(current.getId());
        } catch (IOException ignored) {}
        return StoryResponse.builder()
                .id(s.getId())
                .user(UserSummary.builder()
                        .id(s.getUser().getId())
                        .username(s.getUser().getUsername())
                        .fullName(s.getUser().getFullName())
                        .avatarUrl(s.getUser().getAvatarUrl())
                        .build())
                .mediaUrl(s.getMediaUrl())
                .mediaType(s.getMediaType().name())
                .viewedByMe(viewed)
                .createdAt(s.getCreatedAt())
                .expiresAt(s.getExpiresAt())
                .build();
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
