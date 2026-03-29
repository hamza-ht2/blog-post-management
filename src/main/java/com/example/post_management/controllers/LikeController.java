package com.example.post_management.controllers;

import com.example.post_management.models.User;
import com.example.post_management.services.LikeService;
import com.example.post_management.services.PostService;
import com.example.post_management.services.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/likes")
public class LikeController {
    private final UserService userService;
    private final LikeService likeService;
    private final PostService postService;

    public LikeController(UserService userService, LikeService likeService, PostService postService) {
        this.userService = userService;
        this.likeService = likeService;
        this.postService = postService;
    }

    @PostMapping("/posts/{postId}/like")
    public String likePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails, Model model){
        User user = userService.getUserByUsername(userDetails.getUsername());
        String slug = postService.getPostById(postId).getSlug();
        try{
            likeService.likePost(user.getId(), postId);
        }catch (RuntimeException e){
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/posts/"+slug;
    }
    @PostMapping("/posts/{postId}/unlike")
    public String unlikePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails, Model model){
        User user = userService.getUserByUsername(userDetails.getUsername());
        String slug = postService.getPostById(postId).getSlug();
        try{
            likeService.unlikePost(user.getId(), postId);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/posts/"+slug;
    }

}
