package com.snapora.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts", indexes = {
    @Index(columnList = "user_id"),
    @Index(columnList = "created_at")
})
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 2200)
    private String caption;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(length = 100)
    private String location;

    @Column(name = "is_deleted")
    private boolean deleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostLike> likes = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    public Post() {}

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Set<PostLike> getLikes() { return likes; }
    public Set<Comment> getComments() { return comments; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private User user; private String caption; private String imageUrl; private String location;

        public Builder user(User u) { this.user = u; return this; }
        public Builder caption(String c) { this.caption = c; return this; }
        public Builder imageUrl(String i) { this.imageUrl = i; return this; }
        public Builder location(String l) { this.location = l; return this; }

        public Post build() {
            Post p = new Post();
            p.user = user; p.caption = caption; p.imageUrl = imageUrl; p.location = location;
            return p;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post p)) return false;
        return id != null && id.equals(p.id);
    }

    @Override
    public int hashCode() { return id != null ? id.hashCode() : 0; }
}
