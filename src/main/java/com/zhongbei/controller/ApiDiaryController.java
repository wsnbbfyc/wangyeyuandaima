package com.zhongbei.controller;

import com.zhongbei.model.Diary;
import com.zhongbei.model.Tag;
import com.zhongbei.model.User;
import com.zhongbei.repository.DiaryRepository;
import com.zhongbei.repository.TagRepository;
import com.zhongbei.repository.UserRepository;
import com.zhongbei.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/diaries")
@io.swagger.v3.oas.annotations.tags.Tag(name = "随笔管理", description = "日记/随笔的CRUD操作")
public class ApiDiaryController {

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @Operation(summary = "获取随笔列表", description = "分页获取所有随笔，支持排序")
    public ResponseEntity<Map<String, Object>> getDiaries(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String direction) {
        
        Sort sort = direction.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Page<Diary> diaryPage = diaryRepository.findAll(PageRequest.of(page, size, sort));
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", diaryPage.getContent());
        response.put("totalPages", diaryPage.getTotalPages());
        response.put("totalElements", diaryPage.getTotalElements());
        response.put("currentPage", page);
        response.put("size", size);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取随笔详情", description = "根据ID获取单条随笔")
    public ResponseEntity<Diary> getDiary(
            @Parameter(description = "随笔ID") @PathVariable Long id) {
        Optional<Diary> diary = diaryRepository.findById(id);
        return diary.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "创建随笔", description = "创建新的日记/随笔")
    public ResponseEntity<Diary> createDiary(
            @RequestBody Diary diary,
            Authentication authentication) {
        
        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            Diary newDiary = new Diary();
            newDiary.setTitle(diary.getTitle());
            newDiary.setContent(diary.getContent());
            newDiary.setWeather(diary.getWeather());
            newDiary.setMood(diary.getMood());
            newDiary.setTags(diary.getTags());
            newDiary.setCreateTime(LocalDateTime.now());
            newDiary.setUpdateTime(LocalDateTime.now());
            
            Diary saved = diaryRepository.save(newDiary);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新随笔", description = "更新已有的随笔内容")
    public ResponseEntity<Diary> updateDiary(
            @Parameter(description = "随笔ID") @PathVariable Long id,
            @RequestBody Diary diaryUpdate) {
        
        Optional<Diary> existingDiary = diaryRepository.findById(id);
        if (existingDiary.isPresent()) {
            Diary diary = existingDiary.get();
            diary.setTitle(diaryUpdate.getTitle());
            diary.setContent(diaryUpdate.getContent());
            diary.setWeather(diaryUpdate.getWeather());
            diary.setMood(diaryUpdate.getMood());
            diary.setTags(diaryUpdate.getTags());
            diary.setUpdateTime(LocalDateTime.now());
            
            Diary saved = diaryRepository.save(diary);
            return ResponseEntity.ok(saved);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除随笔", description = "将随笔移至回收站（软删除）")
    public ResponseEntity<Map<String, String>> deleteDiary(
            @Parameter(description = "随笔ID") @PathVariable Long id) {
        
        diaryService.softDelete(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "随笔已移至回收站");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索随笔", description = "根据关键词搜索随笔标题和内容")
    public ResponseEntity<List<Diary>> searchDiaries(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        
        List<Diary> results = diaryService.search(keyword);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/trash")
    @Operation(summary = "获取回收站", description = "获取所有已删除的随笔")
    public ResponseEntity<List<Diary>> getTrash() {
        List<Diary> trashDiaries = diaryService.findAllDeleted();
        return ResponseEntity.ok(trashDiaries);
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "恢复随笔", description = "从回收站恢复随笔")
    public ResponseEntity<Map<String, String>> restoreDiary(
            @Parameter(description = "随笔ID") @PathVariable Long id) {
        
        diaryService.restore(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "随笔已恢复");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/pin")
    @Operation(summary = "置顶/取消置顶", description = "切换随笔的置顶状态")
    public ResponseEntity<Map<String, String>> togglePin(
            @Parameter(description = "随笔ID") @PathVariable Long id) {
        
        diaryService.togglePin(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "置顶状态已切换");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/favorite")
    @Operation(summary = "收藏/取消收藏", description = "切换随笔的收藏状态")
    public ResponseEntity<Map<String, String>> toggleFavorite(
            @Parameter(description = "随笔ID") @PathVariable Long id,
            Authentication authentication) {
        
        String username = authentication.getName();
        diaryService.toggleFavorite(id, username);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "收藏状态已切换");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tag/{tagId}")
    @Operation(summary = "按标签筛选", description = "获取指定标签下的所有随笔")
    public ResponseEntity<List<Diary>> getByTag(
            @Parameter(description = "标签ID") @PathVariable Long tagId) {
        
        List<Diary> diaries = diaryService.findByTagId(tagId);
        return ResponseEntity.ok(diaries);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取统计数据", description = "获取随笔总数等统计信息")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDiaries", diaryRepository.count());
        stats.put("totalTags", tagRepository.count());
        return ResponseEntity.ok(stats);
    }
}
