package com.snapora.service.impl;

import com.snapora.exception.ResourceNotFoundException;
import com.snapora.exception.UnauthorizedException;
import com.snapora.model.dto.response.NotificationResponse;
import com.snapora.model.dto.response.UserSummary;
import com.snapora.model.entity.Notification;
import com.snapora.repository.NotificationRepository;
import com.snapora.repository.UserRepository;
import com.snapora.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<NotificationResponse> getNotifications(String username, Pageable pageable) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(this::toResponse);
    }

    @Override
    public void markAsRead(Long notificationId, String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var notif = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!notif.getRecipient().getId().equals(user.getId()))
            throw new UnauthorizedException("Not your notification");
        notif.setRead(true);
        notificationRepository.save(notif);
    }

    @Override
    public long getUnreadCount(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return notificationRepository.countByRecipientIdAndIsReadFalse(user.getId());
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .actor(UserSummary.builder()
                        .id(n.getActor().getId())
                        .username(n.getActor().getUsername())
                        .fullName(n.getActor().getFullName())
                        .avatarUrl(n.getActor().getAvatarUrl())
                        .build())
                .type(n.getType())
                .referenceId(n.getReferenceId())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
