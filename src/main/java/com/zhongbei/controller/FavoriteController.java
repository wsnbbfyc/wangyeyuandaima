package com.zhongbei.controller;

import com.zhongbei.model.Diary;
import com.zhongbei.model.User;
import com.zhongbei.service.DiaryService;
import com.zhongbei.service.FileStorageService;
import com.zhongbei.service.SiteSettingService;
import com.zhongbei.service.TagService;
import com.zhongbei.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DiaryService diaryService;
    
    @Autowired
    private TagService tagService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private SiteSettingService siteSettingService;
    
    @GetMapping
    public String listFavorites(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName()).orElse(null);
            if (user != null) {
                model.addAttribute("diaries", user.getFavorites());
            }
        }
        model.addAttribute("themePrimaryColor", siteSettingService.getValue("background_color", "#667eea"));
        model.addAttribute("themeBackgroundImage", siteSettingService.getValue("background_image", ""));
        return "favorites/list";
    }
    
    @PostMapping("/add/{diaryId}")
    public String addFavorite(@PathVariable Long diaryId, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName()).orElse(null);
            if (user != null) {
                Diary diary = diaryService.findById(diaryId).orElse(null);
                if (diary != null) {
                    userService.addFavorite(user.getId(), diary);
                    redirectAttributes.addFlashAttribute("success", "已添加到收藏！");
                }
            }
        }
        return "redirect:/diaries/" + diaryId;
    }
    
    @PostMapping("/remove/{diaryId}")
    public String removeFavorite(@PathVariable Long diaryId, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName()).orElse(null);
            if (user != null) {
                Diary diary = diaryService.findById(diaryId).orElse(null);
                if (diary != null) {
                    userService.removeFavorite(user.getId(), diary);
                    redirectAttributes.addFlashAttribute("success", "已从收藏中移除！");
                }
            }
        }
        return "redirect:/diaries/" + diaryId;
    }
}
