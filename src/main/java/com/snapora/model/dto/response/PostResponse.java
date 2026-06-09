package com.snapora.model.dto.response;

import java.time.LocalDateTime;

public class PostResponse {
    private Long id;
    private UserSummary user;
    private String caption;
    private String imageUrl;
    private String location;
    private long likesCount;
    private long commentsCount;
    private boolean likedByMe;
    private boolean savedByMe;
    private LocalDateTime createdAt;

    public PostResponse() {}

    public Long getId() { return id; }
    public UserSummary getUser() { return user; }
    public String getCaption() { return caption; }
    public String getImageUrl() { return imageUrl; }
    public String getLocation() { return location; }
    public long getLikesCount() { return likesCount; }
    public long getCommentsCount() { return commentsCount; }
    public boolean isLikedByMe() { return likedByMe; }
    public boolean isSavedByMe() { return savedByMe; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final PostResponse r = new PostResponse();
        public Builder id(Long v) { r.id = v; return this; }
        public Builder user(UserSummary v) { r.user = v; return this; }
        public Builder caption(String v) { r.caption = v; return this; }
        public Builder imageUrl(String v) { r.imageUrl = v; return this; }
        public Builder location(String v) { r.location = v; return this; }
        public Builder likesCount(long v) { r.likesCount = v; return this; }
        public Builder commentsCount(long v) { r.commentsCount = v; return this; }
        public Builder likedByMe(boolean v) { r.likedByMe = v; return this; }
        public Builder savedByMe(boolean v) { r.savedByMe = v; return this; }
        public Builder createdAt(LocalDateTime v) { r.createdAt = v; return this; }
        public PostResponse build() { return r; }
    }
}
