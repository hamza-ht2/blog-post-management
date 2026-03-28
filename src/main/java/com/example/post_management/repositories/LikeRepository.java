package com.example.post_management.repositories;

import com.example.post_management.models.Like;
import com.example.post_management.models.Post;
import com.example.post_management.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository  extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long PostId);
    Optional<Like> findLikesByUserIdAndPostId(Long userId, Long postId);
    int countLikesByPost(Post post);
    List<Like> findLikesByPostId(Long postId);
    List<Like> findLikesByUserId(Long userId);
    void deleteByUserAndPost(User user, Post post);
}
