package com.snapora.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {
    @Index(columnList = "username"),
    @Index(columnList = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 30)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(length = 150)
    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "is_private")
    private boolean isPrivate = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Follow> following = new HashSet<>();

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Follow> followers = new HashSet<>();

    public User() {}

    public User(Long id, String username, String email, String passwordHash, String fullName,
                String bio, String avatarUrl, boolean isPrivate, LocalDateTime createdAt,
                LocalDateTime updatedAt) {
        this.id = id; this.username = username; this.email = email;
        this.passwordHash = passwordHash; this.fullName = fullName;
        this.bio = bio; this.avatarUrl = avatarUrl; this.isPrivate = isPrivate;
        this.createdAt = createdAt; this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id; private String username; private String email; private String passwordHash;
        private String fullName; private String bio; private String avatarUrl; private boolean isPrivate;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder username(String u) { this.username = u; return this; }
        public Builder email(String e) { this.email = e; return this; }
        public Builder passwordHash(String p) { this.passwordHash = p; return this; }
        public Builder fullName(String f) { this.fullName = f; return this; }
        public Builder bio(String b) { this.bio = b; return this; }
        public Builder avatarUrl(String a) { this.avatarUrl = a; return this; }
        public Builder isPrivate(boolean p) { this.isPrivate = p; return this; }

        public User build() {
            User u = new User();
            u.id = id; u.username = username; u.email = email; u.passwordHash = passwordHash;
            u.fullName = fullName; u.bio = bio; u.avatarUrl = avatarUrl; u.isPrivate = isPrivate;
            return u;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User u)) return false;
        return id != null && id.equals(u.id);
    }

    @Override
    public int hashCode() { return id != null ? id.hashCode() : 0; }
}
