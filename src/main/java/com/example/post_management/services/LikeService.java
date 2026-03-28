package com.example.post_management.services;

import com.example.post_management.models.Like;
import com.example.post_management.models.Post;
import com.example.post_management.models.User;
import com.example.post_management.repositories.LikeRepository;
import com.example.post_management.repositories.PostRepository;
import com.example.post_management.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public LikeService(LikeRepository likeRepository, PostRepository postRepository, UserRepository userRepository){
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public void likePost(Long userId, Long postId){
        Post post = postRepository.findById(postId).orElseThrow(()-> new RuntimeException("post not found"));
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("user not found"));
        if (!user.isEnabled()){
            throw new RuntimeException("you cannot like this post");
        }
        if (likeRepository.existsByUserIdAndPostId(userId, postId)){
            throw new RuntimeException("you already liked this one");
        }
        Like like = new Like(user,post);
        likeRepository.save(like);
    }

    public void unlikePost(Long userId, Long postId){
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("user not found"));
        Post post = postRepository.findById(postId).orElseThrow(()-> new RuntimeException("post not found"));
        if(!likeRepository.existsByUserIdAndPostId(userId,postId)){
            throw new RuntimeException("You have not liked this post");
        }
        likeRepository.deleteByUserAndPost(user,post);
    }
    public int getLikesCount(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(()-> new RuntimeException("post not found"));
        return likeRepository.countLikesByPost(post);
    }
    public List<Like> getLikesByUser(Long userId){
        return likeRepository.findLikesByUserId(userId);
    }
    public List<Like> getLikesByPost(Long postId){
        return likeRepository.findLikesByPostId(postId);
    }
    public Like getLikeByUserIdAndPost(Long userId, Long postId){
        return likeRepository.findLikesByUserIdAndPostId(userId,postId).orElseThrow(()->new RuntimeException("not found"));
    }
}

