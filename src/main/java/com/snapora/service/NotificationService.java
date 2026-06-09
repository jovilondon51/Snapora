package com.snapora.service;

import com.snapora.model.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    Page<NotificationResponse> getNotifications(String username, Pageable pageable);
    void markAsRead(Long notificationId, String username);
    long getUnreadCount(String username);
}
