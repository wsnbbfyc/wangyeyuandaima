package com.zhongbei.controller;

import com.zhongbei.model.SiteSetting;
import com.zhongbei.service.SiteSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/settings")
public class SettingsController {
    
    @Autowired
    private SiteSettingService siteSettingService;
    
    @GetMapping
    public String settingsPage(Model model) {
        String backgroundColor = siteSettingService.getValue("background_color", "#667eea");
        String backgroundImage = siteSettingService.getValue("background_image", "");
        model.addAttribute("backgroundColor", backgroundColor);
        model.addAttribute("backgroundImage", backgroundImage);
        return "settings/index";
    }
    
    @PostMapping("/color")
    public String saveColor(@RequestParam("color") String color, RedirectAttributes redirectAttributes) {
        try {
            siteSettingService.saveSetting("background_color", color, "背景主颜色");
            redirectAttributes.addFlashAttribute("success", "颜色保存成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "保存失败：" + e.getMessage());
        }
        return "redirect:/settings";
    }
    
    @PostMapping("/background")
    public String saveBackground(@RequestParam("background") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "请选择要上传的图片");
                return "redirect:/settings";
            }
            
            String imagePath = siteSettingService.saveBackgroundImage(file);
            siteSettingService.saveSetting("background_image", imagePath, "背景图片路径");
            redirectAttributes.addFlashAttribute("success", "背景图片上传成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "上传失败：" + e.getMessage());
        }
        return "redirect:/settings";
    }
    
    @PostMapping("/reset")
    public String resetSettings(RedirectAttributes redirectAttributes) {
        try {
            siteSettingService.saveSetting("background_color", "#667eea", "背景主颜色");
            siteSettingService.saveSetting("background_image", "", "背景图片路径");
            redirectAttributes.addFlashAttribute("success", "已恢复默认设置！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "重置失败：" + e.getMessage());
        }
        return "redirect:/settings";
    }
}
