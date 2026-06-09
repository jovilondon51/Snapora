package com.snapora.repository;

import com.snapora.model.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Long> {
    @Query("SELECT s FROM Story s WHERE s.user.id IN :userIds AND s.expiresAt > :now ORDER BY s.createdAt DESC")
    List<Story> findActiveStoriesForUsers(List<Long> userIds, LocalDateTime now);

    @Query("SELECT s FROM Story s WHERE s.user.id = :userId AND s.expiresAt > :now ORDER BY s.createdAt ASC")
    List<Story> findActiveStoriesByUser(Long userId, LocalDateTime now);
}
