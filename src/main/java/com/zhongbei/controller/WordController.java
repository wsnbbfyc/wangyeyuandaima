package com.zhongbei.controller;

import com.zhongbei.model.Word;
import com.zhongbei.service.SiteSettingService;
import com.zhongbei.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/words")
public class WordController {
    
    @Autowired
    private WordService wordService;
    
    @Autowired
    private SiteSettingService siteSettingService;
    
    @GetMapping
    public String wordList(Model model) {
        model.addAttribute("themePrimaryColor", siteSettingService.getValue("background_color", "#667eea"));
        model.addAttribute("themeBackgroundImage", siteSettingService.getValue("background_image", ""));
        
        List<Word> words = wordService.findAll();
        model.addAttribute("words", words);
        model.addAttribute("totalCount", wordService.countTotal());
        model.addAttribute("learnedCount", wordService.countLearned());
        model.addAttribute("unlearnedCount", wordService.countUnlearned());
        
        return "words/list";
    }
    
    @GetMapping("/learn")
    public String learn(Model model) {
        model.addAttribute("themePrimaryColor", siteSettingService.getValue("background_color", "#667eea"));
        model.addAttribute("themeBackgroundImage", siteSettingService.getValue("background_image", ""));
        
        List<Word> words = wordService.findWordsForReview(10);
        model.addAttribute("words", words);
        model.addAttribute("currentIndex", 0);
        
        return "words/learn";
    }
    
    @GetMapping("/quiz")
    public String quiz(Model model) {
        model.addAttribute("themePrimaryColor", siteSettingService.getValue("background_color", "#667eea"));
        model.addAttribute("themeBackgroundImage", siteSettingService.getValue("background_image", ""));
        
        List<Word> words = wordService.findRandomWords(10);
        model.addAttribute("words", words);
        model.addAttribute("currentIndex", 0);
        
        return "words/quiz";
    }
    
    @PostMapping("/mark-learned/{id}")
    @ResponseBody
    public String markLearned(@PathVariable Long id) {
        wordService.markAsLearned(id);
        return "success";
    }
    
    @PostMapping("/mark-wrong/{id}")
    @ResponseBody
    public String markWrong(@PathVariable Long id) {
        wordService.markAsWrong(id);
        return "success";
    }
    
    @PostMapping("/reset")
    public String resetProgress() {
        wordService.resetProgress();
        return "redirect:/words";
    }
    
    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        model.addAttribute("themePrimaryColor", siteSettingService.getValue("background_color", "#667eea"));
        model.addAttribute("themeBackgroundImage", siteSettingService.getValue("background_image", ""));
        
        List<Word> words = wordService.search(keyword);
        model.addAttribute("words", words);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalCount", wordService.countTotal());
        model.addAttribute("learnedCount", wordService.countLearned());
        model.addAttribute("unlearnedCount", wordService.countUnlearned());
        
        return "words/list";
    }
}
