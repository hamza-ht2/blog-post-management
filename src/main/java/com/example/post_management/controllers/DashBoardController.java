package com.example.post_management.controllers;

import com.example.post_management.models.User;
import com.example.post_management.services.PostService;
import com.example.post_management.services.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.util.Arrays.stream;

@Controller
@RequestMapping("/dashboard")
public class DashBoardController {
    private final PostService postService;
    private final UserService userService;

    public DashBoardController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }
    @GetMapping
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model){
        User user = userService.getUserByUsername(userDetails.getUsername());
        int totalPosts = postService.getPostsByAuthorId(user.getId()).size();
        int totalPublished = postService.getPublishedPostByAuthor(user.getId()).size();
        int totalDrafts = postService.getDraftsByAuthor(user.getId()).size();
        int totalFollowers = user.getFollowers().size();
        int totalFollowing = user.getFollowing().size();
        int totalViews = postService.getPostsByAuthorId(user.getId())
                .stream()
                .mapToInt(post -> post.getViewCount())
                .sum();
        model.addAttribute("user", user);
        model.addAttribute("totalPosts", totalPosts);
        model.addAttribute("totalPublished", totalPublished);
        model.addAttribute("totalDrafts", totalDrafts);
        model.addAttribute("totalFollowers", totalFollowers);
        model.addAttribute("totalFollowing", totalFollowing);
        model.addAttribute("totalViews", totalViews);

        return "dashboard";

    }
}
