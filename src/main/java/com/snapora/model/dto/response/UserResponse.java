package com.snapora.model.dto.response;

import java.time.LocalDateTime;

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String bio;
    private String avatarUrl;
    private boolean isPrivate;
    private long postsCount;
    private long followersCount;
    private long followingCount;
    private boolean isFollowing;
    private boolean isOwnProfile;
    private LocalDateTime createdAt;

    public UserResponse() {}

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getBio() { return bio; }
    public String getAvatarUrl() { return avatarUrl; }
    public boolean isPrivate() { return isPrivate; }
    public long getPostsCount() { return postsCount; }
    public long getFollowersCount() { return followersCount; }
    public long getFollowingCount() { return followingCount; }
    public boolean isFollowing() { return isFollowing; }
    public boolean isOwnProfile() { return isOwnProfile; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final UserResponse r = new UserResponse();
        public Builder id(Long v) { r.id = v; return this; }
        public Builder username(String v) { r.username = v; return this; }
        public Builder email(String v) { r.email = v; return this; }
        public Builder fullName(String v) { r.fullName = v; return this; }
        public Builder bio(String v) { r.bio = v; return this; }
        public Builder avatarUrl(String v) { r.avatarUrl = v; return this; }
        public Builder isPrivate(boolean v) { r.isPrivate = v; return this; }
        public Builder postsCount(long v) { r.postsCount = v; return this; }
        public Builder followersCount(long v) { r.followersCount = v; return this; }
        public Builder followingCount(long v) { r.followingCount = v; return this; }
        public Builder isFollowing(boolean v) { r.isFollowing = v; return this; }
        public Builder isOwnProfile(boolean v) { r.isOwnProfile = v; return this; }
        public Builder createdAt(LocalDateTime v) { r.createdAt = v; return this; }
        public UserResponse build() { return r; }
    }
}
