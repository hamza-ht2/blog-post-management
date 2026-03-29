package com.example.post_management.controllers;

import com.example.post_management.models.Category;
import com.example.post_management.models.Tag;
import com.example.post_management.models.User;
import com.example.post_management.models.enums.Role;
import com.example.post_management.services.CategoryService;
import com.example.post_management.services.PostService;
import com.example.post_management.services.TagService;
import com.example.post_management.services.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final PostService postService;
    private final CategoryService categoryService;
    private final TagService tagService;

    public AdminController(UserService userService, PostService postService, CategoryService categoryService, TagService tagService) {
        this.userService = userService;
        this.postService = postService;
        this.categoryService = categoryService;
        this.tagService = tagService;
    }
    @ModelAttribute
    public void addAdminToModel(@AuthenticationPrincipal UserDetails userDetails, Model model){
        if(userDetails != null){
            User admin = userService.getUserByUsername(userDetails.getUsername());
            model.addAttribute("admin", admin);
        }
    }
    @GetMapping
    public String adminDashboard(Model model){
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("totalPosts", postService.getAllPosts().size());
        model.addAttribute("totalPublishedPosts", postService.getAllPublishedPosts().size());
        model.addAttribute("totalDrafts", postService.getAllDrafts().size());
        model.addAttribute("totalRejected", postService.getAllRejectedPost().size());
        return "admin/dashboard";
    }
    @GetMapping("/users")
    public String getAllUsers(Model model){
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }
    @GetMapping("/users/disabled")
    public String getAllDisabledUsers(Model model){
        model.addAttribute("users", userService.getUsersByEnabled(false));
        return "admin/users";
    }
    @GetMapping("/users/authors")
    public String getAuthors(Model model){
        model.addAttribute("users", userService.getUsersByRole(Role.AUTHOR));
        return "admin/users";
    }
    @GetMapping("/users/{userId}")
    public String getUserById(@PathVariable Long userId,  Model model){
        model.addAttribute("user", userService.getUserById(userId));
        model.addAttribute("posts", postService.getPostsByAuthorId(userId));
        return "admin/user-detail";
    }
    @PostMapping("/users/{userId}/enable")
    public String enableUser(@PathVariable Long userId){
        try{
            userService.enableUser(userId);
        }catch (RuntimeException e){
            return "redirect:/admin/users?error=true";
        }
        return "redirect:/admin/users/"+userId;
    }
    @PostMapping("/users/{userId}/disable")
    public String disableUser(@PathVariable Long userId){
        try{
            userService.disableUser(userId);
        }catch (RuntimeException e){
            return "redirect:/admin/users?error=true";
        }
        return "redirect:/admin/users/"+userId;
    }
    @PostMapping("/users/{userId}/role")
    public String changeRole(@PathVariable Long userId, @RequestParam String role){
        try{
            userService.changeRole(userId, Role.valueOf(role));
        }catch (RuntimeException e){
            return "redirect:/admin/users?error=true";
        }
        return "redirect:/admin/users/"+userId;
    }
    @PostMapping("/users/{userId}/delete")
    public String deleteUser(@PathVariable Long userId){
        try {
            userService.deleteUser(userId);
        }catch (RuntimeException e){
            return "redirect:/admin/users?error=true";
        }
        return "redirect:/admin/users";
    }
    // ------------ POSTS ---------------

    @GetMapping("/posts")
    public String getAllPosts(Model model){
        model.addAttribute("posts",postService.getAllPosts());
        return "admin/posts";
    }
    @GetMapping("/posts/rejected")
    public String getAllRejectedPosts(Model model){
        model.addAttribute("posts", postService.getAllRejectedPost());
        return "admin/rejected-posts";
    }
    @PostMapping("/posts/{id}/reject")
    public String rejectPost(@PathVariable Long id){
        try{
            postService.rejectPost(id);
        }catch (RuntimeException e){
            return "redirect:/admin/posts?error=true";
        }
        return "redirect:/admin/posts";
    }
    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id){
        try{
            postService.deletePost(id);
        }catch (RuntimeException e){
            return "redirect:/admin/posts?error=true";
        }
        return "redirect:/admin/posts";
    }

    // ------ Categories ----------
    @GetMapping("/categories")
    public String getAllCategories(Model model){
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/categories";
    }
    @GetMapping("/categories/create")
    public String createCategoryForm(Model model){
        model.addAttribute("category", new Category());
        return "admin/category-create";
    }
    @PostMapping("/categories/create")
    public String saveCategory(@ModelAttribute Category category, Model model){
        try{
            categoryService.createCategory(category);
        }catch (RuntimeException e){
            model.addAttribute("error", e.getMessage());
            return "admin/category-create";
        }
        return "redirect:/admin/categories";
    }
    @GetMapping("/categories/{id}")
    public String getCategoryById(@PathVariable Long id, Model model){
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "admin/category-detail";
    }
    @GetMapping("/categories/{id}/edit")
    public String editCategoryForm(@PathVariable Long id,  Model model){
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "admin/category-edit";
    }
    @PostMapping("/categories/{id}/edit")
    public String updateCategory(@PathVariable Long id, @ModelAttribute Category category, Model model){
        try{
            categoryService.updateCategory(id, category);
        }catch (RuntimeException e){
            model.addAttribute("error", e.getMessage());
            return "admin/category-edit";
        }
        return "redirect:/admin/categories/"+id;
    }
    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, Model model){
        try{
            categoryService.deleteCategory(id);
        }catch (RuntimeException e){
            model.addAttribute("error", e.getMessage());
            return "redirect:/admin/categories?error=true";
        }
        return "redirect:/admin/categories";
    }

    // ------- Tags ---------
    @GetMapping("/tags")
    public String getTags(Model model){
        model.addAttribute("tags", tagService.getAllTags());
        return "admin/tags";
    }
    @GetMapping("/tags/{id}")
    public String getTagById(@PathVariable Long id, Model model){
        model.addAttribute("tag", tagService.getTagById(id));
        return "admin/tag-detail";
    }
    @GetMapping("/tags/create")
    public String createTagForm(Model model){
        model.addAttribute("tag", new Tag());
        return "admin/tag-create";
    }
    @PostMapping("/tags/create")
    public String saveTag(@ModelAttribute Tag tag, Model model){
        try{
            tagService.createTag(tag);
        }catch (RuntimeException e){
            model.addAttribute("error", e.getMessage());
            return "admin/tag-create";
        }
        return "redirect:/admin/tags";
    }
    @GetMapping("/tags/{id}/edit")
    public String editTagForm(@PathVariable Long id, Model model){
        model.addAttribute("tag", tagService.getTagById(id));
        return "admin/tag-edit";
    }
    @PostMapping("/tags/{id}/edit")
    public String updateTag(@PathVariable Long id, @ModelAttribute Tag tag, Model model){
        try{
            tagService.updateTagInfo(id, tag);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/tag-edit";
        }
        return "redirect:/admin/tags/"+id;
    }
    @PostMapping("/tags/{id}/delete")
    public String deleteTag(@PathVariable Long id, Model model){
        try{
            tagService.deleteTag(id);
        } catch (RuntimeException e) {
            model.addAttribute("error",e.getMessage());
            return "redirect:/admin/tags/"+id+"?error=true";
        }
        return "redirect:/admin/tags";
    }

}
