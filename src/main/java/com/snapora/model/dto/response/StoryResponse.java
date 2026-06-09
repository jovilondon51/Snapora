package com.snapora.model.dto.response;

import java.time.LocalDateTime;

public class StoryResponse {
    private Long id;
    private UserSummary user;
    private String mediaUrl;
    private String mediaType;
    private boolean viewedByMe;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public StoryResponse() {}

    public Long getId() { return id; }
    public UserSummary getUser() { return user; }
    public String getMediaUrl() { return mediaUrl; }
    public String getMediaType() { return mediaType; }
    public boolean isViewedByMe() { return viewedByMe; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final StoryResponse r = new StoryResponse();
        public Builder id(Long v) { r.id = v; return this; }
        public Builder user(UserSummary v) { r.user = v; return this; }
        public Builder mediaUrl(String v) { r.mediaUrl = v; return this; }
        public Builder mediaType(String v) { r.mediaType = v; return this; }
        public Builder viewedByMe(boolean v) { r.viewedByMe = v; return this; }
        public Builder createdAt(LocalDateTime v) { r.createdAt = v; return this; }
        public Builder expiresAt(LocalDateTime v) { r.expiresAt = v; return this; }
        public StoryResponse build() { return r; }
    }
}
