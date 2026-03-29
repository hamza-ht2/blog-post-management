package com.example.post_management.controllers;

import com.example.post_management.models.User;
import com.example.post_management.services.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    public AuthController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(){
        return "auth/login";
    }
    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("user", new User());
        return "auth/register";
    }
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute User user, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()){
            return "auth/register";
        }
        try{
            userService.register(user);
            return "redirect:/auth/login?registered=true";
        }catch (RuntimeException e){
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

}
