package com.zhongbei.controller;

import com.zhongbei.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "用户名或密码错误");
        }
        if (logout != null) {
            model.addAttribute("message", "已成功退出登录");
        }
        return "auth/login";
    }
    
    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String register(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          @RequestParam String email,
                          @RequestParam(required = false) String nickname,
                          Model model) {
        try {
            if (userService.isUsernameExists(username)) {
                model.addAttribute("error", "用户名已存在");
                return "auth/register";
            }
            
            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "两次输入的密码不一致");
                return "auth/register";
            }
            
            if (password.length() < 6) {
                model.addAttribute("error", "密码长度至少6位");
                return "auth/register";
            }
            
            userService.register(username, password, email, nickname);
            model.addAttribute("success", "注册成功！请登录");
            return "auth/login";
            
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }
}
