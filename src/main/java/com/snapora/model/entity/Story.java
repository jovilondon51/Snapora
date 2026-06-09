package com.snapora.model.entity;

import com.snapora.model.enums.MediaType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "stories", indexes = {
    @Index(columnList = "user_id"), @Index(columnList = "created_at"), @Index(columnList = "expires_at")
})
public class Story {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "media_url", nullable = false)
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaType mediaType = MediaType.IMAGE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "viewed_by", columnDefinition = "JSON")
    private String viewedBy = "[]";

    public Story() {}

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public MediaType getMediaType() { return mediaType; }
    public void setMediaType(MediaType mediaType) { this.mediaType = mediaType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public String getViewedBy() { return viewedBy; }
    public void setViewedBy(String viewedBy) { this.viewedBy = viewedBy; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private User user; private String mediaUrl; private MediaType mediaType = MediaType.IMAGE; private LocalDateTime expiresAt;
        public Builder user(User u) { this.user = u; return this; }
        public Builder mediaUrl(String m) { this.mediaUrl = m; return this; }
        public Builder mediaType(MediaType t) { this.mediaType = t; return this; }
        public Builder expiresAt(LocalDateTime e) { this.expiresAt = e; return this; }
        public Story build() {
            Story s = new Story(); s.user = user; s.mediaUrl = mediaUrl; s.mediaType = mediaType; s.expiresAt = expiresAt; return s;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Story s)) return false;
        return id != null && id.equals(s.id);
    }
    @Override public int hashCode() { return id != null ? id.hashCode() : 0; }
}
