package com.example.post_management.controllers;

import com.example.post_management.models.User;
import com.example.post_management.services.CommentService;
import com.example.post_management.services.PostService;
import com.example.post_management.services.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;
    private final PostService postService;

    public CommentController(CommentService commentService, UserService userService, PostService postService) {
        this.commentService = commentService;
        this.userService = userService;
        this.postService = postService;
    }

    @PostMapping("/post/{postId}/add")
    public String addComment(@PathVariable Long postId, @RequestParam String content, @AuthenticationPrincipal UserDetails userDetails, Model model){
        try{
            User user = userService.getUserByUsername(userDetails.getUsername());
            commentService.addCommentToPost(user.getId(),postId,content);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        String slug = postService.getPostById(postId).getSlug();
        return "redirect:/posts/"+slug;
    }
    @PostMapping("/{commentId}/edit")
    public String editComment(@PathVariable Long commentId, @RequestParam String content, @AuthenticationPrincipal UserDetails userDetails, Model model){
        try{
            User user = userService.getUserByUsername(userDetails.getUsername());
            commentService.editComment(commentId, user.getId(), content);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        String slug = commentService.getCommentById(commentId).getPost().getSlug();
        return "redirect:/posts/"+slug;
    }
    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal UserDetails userDetails){
        User user = userService.getUserByUsername(userDetails.getUsername());
        String slug = commentService.getCommentById(commentId).getPost().getSlug();
        commentService.deleteComment(commentId, user.getId());
        return "redirect:/posts/"+slug;
    }
}
