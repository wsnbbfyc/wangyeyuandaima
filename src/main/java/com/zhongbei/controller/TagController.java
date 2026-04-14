package com.zhongbei.controller;

import com.zhongbei.model.Diary;
import com.zhongbei.model.Tag;
import com.zhongbei.service.DiaryService;
import com.zhongbei.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tags")
public class TagController {
    
    @Autowired
    private TagService tagService;
    
    @Autowired
    private DiaryService diaryService;
    
    @GetMapping
    public String listTags(Model model) {
        model.addAttribute("tags", tagService.findAll());
        return "tags/list";
    }
    
    @GetMapping("/new")
    public String newTagForm(Model model) {
        model.addAttribute("tag", new Tag());
        return "tags/new";
    }
    
    @PostMapping
    public String createTag(@ModelAttribute Tag tag, RedirectAttributes redirectAttributes) {
        if (tagService.existsByName(tag.getName())) {
            redirectAttributes.addFlashAttribute("error", "标签名称已存在");
            return "redirect:/tags/new";
        }
        tagService.save(tag);
        redirectAttributes.addFlashAttribute("success", "标签创建成功！");
        return "redirect:/tags";
    }
    
    @PostMapping("/{id}/delete")
    public String deleteTag(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        tagService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "标签删除成功！");
        return "redirect:/tags";
    }
    
    @GetMapping("/{id}/diaries")
    public String listDiariesByTag(@PathVariable Long id, Model model) {
        Tag tag = tagService.findById(id);
        if (tag == null) {
            return "redirect:/tags";
        }
        List<Diary> diaries = diaryService.findByTagId(id);
        model.addAttribute("tag", tag);
        model.addAttribute("diaries", diaries);
        return "tags/diaries";
    }
}
