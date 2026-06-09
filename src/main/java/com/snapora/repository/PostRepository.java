package com.snapora.repository;

import com.snapora.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.user.id = :userId AND p.isDeleted = false ORDER BY p.createdAt DESC")
    Page<Post> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.user.id IN :followingIds AND p.isDeleted = false ORDER BY p.createdAt DESC")
    Page<Post> findFeedPosts(java.util.List<Long> followingIds, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.isDeleted = false ORDER BY SIZE(p.likes) DESC, p.createdAt DESC")
    Page<Post> findExplorePosts(Pageable pageable);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.user.id = :userId AND p.isDeleted = false")
    long countByUserId(Long userId);
}
