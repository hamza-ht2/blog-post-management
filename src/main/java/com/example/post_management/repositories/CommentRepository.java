package com.example.post_management.repositories;

import com.example.post_management.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findCommentsByUserId(Long userId);
    List<Comment> findCommentsByPostId(Long postId);
    List<Comment> findCommentsByUserIdAndPostId(Long userId, Long postId);
}
