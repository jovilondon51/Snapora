package com.snapora.controller;

import com.snapora.model.dto.response.NotificationResponse;
import com.snapora.repository.UserRepository;
import com.snapora.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Map<Long, SseEmitter> sseEmitters;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(@AuthenticationPrincipal UserDetails user,
                                                                        Pageable pageable) {
        return ResponseEntity.ok(notificationService.getNotifications(user.getUsername(), pageable));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        notificationService.markAsRead(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(user.getUsername())));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@AuthenticationPrincipal UserDetails user) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        userRepository.findByUsername(user.getUsername()).ifPresent(u -> {
            sseEmitters.put(u.getId(), emitter);
            emitter.onCompletion(() -> sseEmitters.remove(u.getId()));
            emitter.onTimeout(() -> sseEmitters.remove(u.getId()));
        });
        return emitter;
    }
}
