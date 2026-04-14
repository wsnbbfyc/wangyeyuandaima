package com.zhongbei.controller;

import com.zhongbei.model.Diary;
import com.zhongbei.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
public class ExportController {
    
    @Autowired
    private DiaryService diaryService;
    
    @GetMapping("/export/markdown/{id}")
    public ResponseEntity<byte[]> exportMarkdown(@PathVariable Long id) throws IOException {
        Optional<Diary> diaryOpt = diaryService.findById(id);
        
        if (!diaryOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Diary diary = diaryOpt.get();
        
        StringBuilder markdown = new StringBuilder();
        markdown.append("# ").append(diary.getTitle()).append("\n\n");
        markdown.append("**创建时间**: ").append(diary.getCreateTime().toString()).append("\n\n");
        
        if (diary.getTags() != null && !diary.getTags().isEmpty()) {
            markdown.append("**标签**: ");
            diary.getTags().forEach(tag -> markdown.append(tag.getName()).append(", "));
            markdown.append("\n\n");
        }
        
        if (diary.getWeather() != null) {
            markdown.append("**天气**: ").append(diary.getWeather()).append("\n\n");
        }
        
        if (diary.getMood() != null) {
            markdown.append("**心情**: ").append(diary.getMood()).append("\n\n");
        }
        
        markdown.append("---\n\n");
        markdown.append(diary.getContent());
        
        String fileName = diary.getTitle().replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5]", "_") + ".md";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/markdown;charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", fileName);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(markdown.toString().getBytes("UTF-8"));
    }
}
