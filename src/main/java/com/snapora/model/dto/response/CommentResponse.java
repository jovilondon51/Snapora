package com.snapora.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {
    private Long id;
    private UserSummary user;
    private String content;
    private Long parentId;
    private List<CommentResponse> replies;
    private LocalDateTime createdAt;

    public CommentResponse() {}

    public Long getId() { return id; }
    public UserSummary getUser() { return user; }
    public String getContent() { return content; }
    public Long getParentId() { return parentId; }
    public List<CommentResponse> getReplies() { return replies; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final CommentResponse r = new CommentResponse();
        public Builder id(Long v) { r.id = v; return this; }
        public Builder user(UserSummary v) { r.user = v; return this; }
        public Builder content(String v) { r.content = v; return this; }
        public Builder parentId(Long v) { r.parentId = v; return this; }
        public Builder replies(List<CommentResponse> v) { r.replies = v; return this; }
        public Builder createdAt(LocalDateTime v) { r.createdAt = v; return this; }
        public CommentResponse build() { return r; }
    }
}
