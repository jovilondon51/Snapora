package com.snapora.model.dto.response;

import com.snapora.model.enums.NotificationType;
import java.time.LocalDateTime;

public class NotificationResponse {
    private Long id;
    private UserSummary actor;
    private NotificationType type;
    private Long referenceId;
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationResponse() {}

    public Long getId() { return id; }
    public UserSummary getActor() { return actor; }
    public NotificationType getType() { return type; }
    public Long getReferenceId() { return referenceId; }
    public boolean isRead() { return isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final NotificationResponse r = new NotificationResponse();
        public Builder id(Long v) { r.id = v; return this; }
        public Builder actor(UserSummary v) { r.actor = v; return this; }
        public Builder type(NotificationType v) { r.type = v; return this; }
        public Builder referenceId(Long v) { r.referenceId = v; return this; }
        public Builder isRead(boolean v) { r.isRead = v; return this; }
        public Builder createdAt(LocalDateTime v) { r.createdAt = v; return this; }
        public NotificationResponse build() { return r; }
    }
}
