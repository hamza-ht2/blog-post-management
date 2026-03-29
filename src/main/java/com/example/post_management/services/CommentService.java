package com.example.post_management.services;

import com.example.post_management.models.Comment;
import com.example.post_management.models.Post;
import com.example.post_management.models.User;
import com.example.post_management.models.enums.Role;
import com.example.post_management.repositories.CommentRepository;
import com.example.post_management.repositories.PostRepository;
import com.example.post_management.repositories.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public Comment addCommentToPost(Long userId, Long postId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("post not found"));
        if (!user.isEnabled()) {
            throw new RuntimeException("your account is disabled, you cannot comment");
        }
        if (content == null || content.isBlank()) {
            throw new RuntimeException("comment cannot be empty");
        }
        Comment comment = new Comment(content, post, user);
        return commentRepository.save(comment);
    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("comment not found"));
    }

    public List<Comment> getCommentsByUserId(Long userId) {
        return commentRepository.findCommentsByUserId(userId);
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findCommentsByPostId(postId);
    }

    public List<Comment> getCommentsByUserOnSpecificPost(Long userId, Long postId) {
        return commentRepository.findCommentsByUserIdAndPostId(userId, postId);
    }

    public Comment editComment(Long commentId, Long userId, String newContent) {
        Comment comment = getCommentById(commentId);
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("you cannot edit this comment");
        }
        if (newContent == null || newContent.isBlank()) {
            throw new RuntimeException("comment cannot be empty");
        }
        comment.setContent(newContent);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, Long userId) {
        Comment comment = getCommentById(commentId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found"));
        boolean isOwner = comment.getUser().getId().equals(userId);
        boolean isAdmin = user.getRole().equals(Role.ADMIN);
        if (!isOwner && !isAdmin) {
            throw new RuntimeException("you cannot delete this comment");
        }
        commentRepository.delete(comment);
    }
}