package com.example.post_management.controllers;

import com.example.post_management.services.CategoryService;
import com.example.post_management.services.PostService;
import com.example.post_management.services.TagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    private final PostService postService;
    private final CategoryService categoryService;
    private final TagService tagService;

    public HomeController(PostService postService, CategoryService categoryService, TagService tagService) {
        this.postService = postService;
        this.categoryService = categoryService;
        this.tagService = tagService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model){
        model.addAttribute("posts", postService.getAllPublishedPosts());
        model.addAttribute("categories",categoryService.getAllCategories());
        model.addAttribute("tags", tagService.getAllTags());
        return "home";
    }
}
