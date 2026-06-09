package com.snapora.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "saved_posts",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"}),
    indexes = { @Index(columnList = "user_id"), @Index(columnList = "created_at") })
public class SavedPost {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public SavedPost() {}

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private User user; private Post post;
        public Builder user(User u) { this.user = u; return this; }
        public Builder post(Post p) { this.post = p; return this; }
        public SavedPost build() { SavedPost sp = new SavedPost(); sp.user = user; sp.post = post; return sp; }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SavedPost sp)) return false;
        return id != null && id.equals(sp.id);
    }
    @Override public int hashCode() { return id != null ? id.hashCode() : 0; }
}
