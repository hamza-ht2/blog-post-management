package com.example.post_management.controllers;

import com.example.post_management.models.User;
import com.example.post_management.services.PostService;
import com.example.post_management.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProfileController {
    private final UserService userService;
    private final PostService postService;

    public ProfileController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }
    @GetMapping("/authors/{username}")
    public String viewAuthorProfile(@PathVariable String username, Model model){
        User author = userService.getUserByUsername(username);
        model.addAttribute("author", author);
        model.addAttribute("posts", postService.getPublishedPostByAuthor(author.getId()));
        model.addAttribute("totalFollowers", author.getFollowers().size());
        model.addAttribute("totalFollowing", author.getFollowing().size());
        return "profile/view";
    }
    @GetMapping("/profile/posts")
    public String myPosts(@AuthenticationPrincipal UserDetails userDetails, Model model){
        User user = userService.getUserByUsername(userDetails.getUsername());
        model.addAttribute("posts", postService.getPostsByAuthorId(user.getId()));
        return "profile/posts";
    }
    @GetMapping("/profile/drafts")
    public String myDrafts(@AuthenticationPrincipal UserDetails userDetails, Model model){
        User user = userService.getUserByUsername(userDetails.getUsername());
        model.addAttribute("drafts", postService.getDraftsByAuthor(user.getId()));
        return "profile/drafts";
    }
    @GetMapping("/profile/rejected")
    public String rejectedPosts(@AuthenticationPrincipal UserDetails userDetails, Model model){
        User user = userService.getUserByUsername(userDetails.getUsername());
        model.addAttribute("rejected", postService.getAllRejectedPost());
        return "profile/rejected";
    }
    @GetMapping("/profile/settings")
    public String settingsPage(@AuthenticationPrincipal UserDetails userDetails, Model model){
        User user = userService.getUserByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        return "profile/settings";
    }
    @PostMapping("/profile/settings")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute User updatedUser,  Model model){
        try{
            User user = userService.getUserByUsername(userDetails.getUsername());
            userService.updateProfile(user.getId(), updatedUser);
            return "redirect:/profile/settings?success=true";
        }catch (RuntimeException e){
            model.addAttribute("error", e.getMessage());
            return "profile/settings";
        }
    }
    @PostMapping("/profile/password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String oldPassword, @RequestParam String newPassword, Model model){
        try{
            User user = userService.getUserByUsername(userDetails.getUsername());
            userService.updatePassword(user.getId(), oldPassword, newPassword);
            return "redirect:/profile/settings?passwordChanged=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "profile/settings";
        }
    }
    @PostMapping("/profile/coverImage")
    public String changeCover(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String profileImage, Model model){
        try{
            User user = userService.getUserByUsername(userDetails.getUsername());
            userService.updatePhotoProfile(user.getId(), profileImage);
            return "redirect:/profile/settings?profileChanged=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "profile/settings";
        }
    }
    @PostMapping("/authors/{username}/follow")
    public String follow(@PathVariable String username, @AuthenticationPrincipal UserDetails userDetails){
        User follower = userService.getUserByUsername(userDetails.getUsername());
        User following = userService.getUserByUsername(username);
        userService.follow(follower.getId(), following.getId());
        return "redirect:/authors/"+username;
    }
    @PostMapping("/authors/{username}/unfollow")
    public String unfollow(@PathVariable String username, @AuthenticationPrincipal UserDetails userDetails){
        User follower = userService.getUserByUsername(userDetails.getUsername());
        User following = userService.getUserByUsername(username);
        userService.unfollow(follower.getId(), following.getId());
        return "redirect:/authors/"+username;
    }
}
