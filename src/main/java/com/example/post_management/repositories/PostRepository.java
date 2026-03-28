package com.example.post_management.repositories;

import com.example.post_management.models.Category;
import com.example.post_management.models.Post;
import com.example.post_management.models.Tag;
import com.example.post_management.models.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findPostBySlug(String slug);
    List<Post> findPostsByCategoryId(Long categoryId);
    List<Post> findPostByPostStatus(PostStatus postStatus);
    List<Post> findPostsByAuthorId(Long userId);
    List<Post> findPostsByAuthorIdAndPostStatus(Long userId, PostStatus postStatus);
    List<Post> findPostsByPostStatusAndCategoryId(PostStatus postStatus, Long categoryId);
    List<Post> findPostsByTagsContaining(Tag tag);
    boolean existsBySlug(String slug);
    List<Post> findPostsByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Post> findPostsByPostStatusOrderByCreatedAtDesc(PostStatus status);
    List<Post> findPostsByTitleContainingIgnoreCase(String title);
}
