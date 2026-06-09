package com.snapora.service;

import com.snapora.model.dto.response.StoryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StoryService {
    StoryResponse createStory(String username, MultipartFile media);
    List<StoryResponse> getFeedStories(String username);
    StoryResponse viewStory(Long storyId, String username);
}
