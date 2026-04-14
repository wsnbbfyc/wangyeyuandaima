package com.zhongbei.controller;

import com.zhongbei.model.Diary;
import com.zhongbei.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DiaryController {
    
    @Autowired
    private DiaryService diaryService;
    
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("diaries", diaryService.findAll());
        return "index";
    }
    
    @GetMapping("/diaries")
    public String listDiaries(Model model) {
        model.addAttribute("diaries", diaryService.findAll());
        return "diary/list";
    }
    
    @GetMapping("/diaries/new")
    public String newDiaryForm(Model model) {
        model.addAttribute("diary", new Diary());
        return "diary/new";
    }
    
    @PostMapping("/diaries")
    public String createDiary(@ModelAttribute Diary diary, RedirectAttributes redirectAttributes) {
        diaryService.save(diary);
        redirectAttributes.addFlashAttribute("success", "日记创建成功！");
        return "redirect:/diaries";
    }
    
    @GetMapping("/diaries/{id}")
    public String viewDiary(@PathVariable Long id, Model model) {
        Diary diary = diaryService.findById(id).orElseThrow(() -> new RuntimeException("日记不存在"));
        model.addAttribute("diary", diary);
        return "diary/view";
    }
    
    @GetMapping("/diaries/{id}/edit")
    public String editDiaryForm(@PathVariable Long id, Model model) {
        Diary diary = diaryService.findById(id).orElseThrow(() -> new RuntimeException("日记不存在"));
        model.addAttribute("diary", diary);
        return "diary/edit";
    }
    
    @PostMapping("/diaries/{id}")
    public String updateDiary(@PathVariable Long id, @ModelAttribute Diary diary, RedirectAttributes redirectAttributes) {
        diary.setId(id);
        diaryService.save(diary);
        redirectAttributes.addFlashAttribute("success", "日记更新成功！");
        return "redirect:/diaries/" + id;
    }
    
    @PostMapping("/diaries/{id}/delete")
    public String deleteDiary(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        diaryService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "日记删除成功！");
        return "redirect:/diaries";
    }
}
