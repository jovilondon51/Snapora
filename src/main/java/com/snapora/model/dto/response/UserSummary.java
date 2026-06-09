package com.snapora.model.dto.response;

public class UserSummary {
    private Long id;
    private String username;
    private String fullName;
    private String avatarUrl;
    private boolean isFollowing;

    public UserSummary() {}

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getAvatarUrl() { return avatarUrl; }
    public boolean isFollowing() { return isFollowing; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final UserSummary u = new UserSummary();
        public Builder id(Long v) { u.id = v; return this; }
        public Builder username(String v) { u.username = v; return this; }
        public Builder fullName(String v) { u.fullName = v; return this; }
        public Builder avatarUrl(String v) { u.avatarUrl = v; return this; }
        public Builder isFollowing(boolean v) { u.isFollowing = v; return this; }
        public UserSummary build() { return u; }
    }
}
