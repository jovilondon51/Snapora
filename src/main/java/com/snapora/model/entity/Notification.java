package com.snapora.model.entity;

import com.snapora.model.enums.NotificationType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
    @Index(columnList = "recipient_id"), @Index(columnList = "created_at")
})
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "is_read")
    private boolean isRead = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Notification() {}

    public Long getId() { return id; }
    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }
    public User getActor() { return actor; }
    public void setActor(User actor) { this.actor = actor; }
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private User recipient; private User actor; private NotificationType type; private Long referenceId;
        public Builder recipient(User r) { this.recipient = r; return this; }
        public Builder actor(User a) { this.actor = a; return this; }
        public Builder type(NotificationType t) { this.type = t; return this; }
        public Builder referenceId(Long id) { this.referenceId = id; return this; }
        public Notification build() {
            Notification n = new Notification(); n.recipient = recipient; n.actor = actor; n.type = type; n.referenceId = referenceId; return n;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification n)) return false;
        return id != null && id.equals(n.id);
    }
    @Override public int hashCode() { return id != null ? id.hashCode() : 0; }
}
