package com.example.post_management.controllers;

import com.example.post_management.models.Like;
import com.example.post_management.models.Post;
import com.example.post_management.models.User;
import com.example.post_management.services.*;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final CommentService commentService;
    private final LikeService likeService;

    public PostController(PostService postService, UserService userService, CategoryService categoryService, TagService tagService, CommentService commentService, LikeService likeService) {
        this.postService = postService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.tagService = tagService;
        this.commentService = commentService;
        this.likeService = likeService;
    }

    // ---------PUBLIC-------------//
    @GetMapping
    public String showPosts(Model model){
        model.addAttribute("posts", postService.getAllPublishedPosts());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "posts/list";
    }

    @GetMapping("/{slug}")
    public String getPostBySlug(@PathVariable String slug, Model model){
        Post post = postService.getPostBySlug(slug);
        postService.incrementView(post.getId());
        model.addAttribute("post", post);
        model.addAttribute("comments",commentService.getCommentsByPostId(post.getId()));
        model.addAttribute("likesCount", likeService.getLikesCount(post.getId()));
        return "posts/detail";
    }
    @GetMapping("/search")
    public String searchPosts(@RequestParam String title, Model model){
        model.addAttribute("posts", postService.searchPostsByTitle(title));
        return "posts/search";
    }
    @GetMapping("/category/{categoryId}")
    public String getPostsByCategory(@PathVariable Long categoryId, Model model){
        model.addAttribute("posts", postService.getPostsByCategory(categoryId));
        model.addAttribute("category", categoryService.getCategoryById(categoryId));
        return "posts/list";
    }

    //--------AUTHENTICATED------------

    @GetMapping("/create")
    public String showPostForm(Model model){
        model.addAttribute("post", new Post());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("tags", tagService.getAllTags());
        return "posts/create";
    }

    @PostMapping("/create")
    public String savePost(@Valid @ModelAttribute Post post, BindingResult result , @AuthenticationPrincipal UserDetails userDetails, Model model){
        if (result.hasErrors()){
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("tags", tagService.getAllTags());
            return "posts/create";
        }
        try{
            User author = userService.getUserByUsername(userDetails.getUsername());
            Post created = postService.createPost(author.getId(), post);
            return "redirect:/posts/" + created.getSlug();
        }catch (RuntimeException e){
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("tags", tagService.getAllTags());
            return "posts/create";
        }
    }
    @GetMapping("/{id}/edit")
    public String showEditPostForm(@PathVariable Long id, Model model){
        model.addAttribute("post", postService.getPostById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("tags", tagService.getAllTags());
        return "posts/edit";
    }
    @PostMapping("/{id}/edit")
    public String editPost(@PathVariable Long id, @Valid @ModelAttribute Post post, BindingResult result , @AuthenticationPrincipal UserDetails userDetails, Model model){
        if (result.hasErrors()){
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("tags", tagService.getAllTags());
            return "posts/edit";
        }
        try{
            Post existing = postService.updatePost(id,post);
            return "redirect:/posts/" + existing.getSlug();
        }catch (RuntimeException e){
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("tags", tagService.getAllTags());
            return "posts/edit";
        }
    }
    @PostMapping("/{id}/publish")
    public String publishPost(@PathVariable Long id){
        postService.publishPost(id);
        return "redirect:/dashboard";
    }
    @PostMapping("/{id}/unpublish")
    public String unpublishPost(@PathVariable Long id){
        postService.unpublishPost(id);
        return "redirect:/dashboard";
    }
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id){
        postService.deletePost(id);
        return "redirect:/dashboard";
    }
    @PostMapping("/{postId}/tags/{tagId}/add")
    public String addTag(@PathVariable Long postId , @PathVariable Long tagId){
        postService.addTagsToPost(postId, tagId);
        return "redirect:/posts/"+ postId +"/edit";
    }
    @PostMapping("/{postId}/tags/{tagId}/remove")
    public String removeTag(@PathVariable Long postId, @PathVariable Long tagId){
        postService.removeTagsFromPost(postId, tagId);
        return "redirect:/posts/"+postId+"/edit";
    }
}
