package com.zhongbei.controller;

import com.zhongbei.model.Tag;
import com.zhongbei.repository.TagRepository;
import com.zhongbei.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tags")
@io.swagger.v3.oas.annotations.tags.Tag(name = "标签管理", description = "标签的CRUD操作")
public class ApiTagController {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagRepository tagRepository;

    @GetMapping
    @Operation(summary = "获取所有标签", description = "返回标签列表")
    public ResponseEntity<List<Tag>> getAllTags() {
        List<Tag> tags = tagService.findAll();
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取标签详情", description = "根据ID获取标签")
    public ResponseEntity<Tag> getTag(
            @Parameter(description = "标签ID") @PathVariable Long id) {
        Optional<Tag> tag = tagRepository.findById(id);
        return tag.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "创建标签", description = "创建新标签")
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        Tag saved = tagRepository.save(tag);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新标签", description = "更新标签名称")
    public ResponseEntity<Tag> updateTag(
            @Parameter(description = "标签ID") @PathVariable Long id,
            @RequestBody Tag tagUpdate) {
        Optional<Tag> existing = tagRepository.findById(id);
        if (existing.isPresent()) {
            Tag tag = existing.get();
            tag.setName(tagUpdate.getName());
            tag.setColor(tagUpdate.getColor());
            Tag saved = tagRepository.save(tag);
            return ResponseEntity.ok(saved);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除标签", description = "根据ID删除标签")
    public ResponseEntity<Void> deleteTag(
            @Parameter(description = "标签ID") @PathVariable Long id) {
        tagRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    @Operation(summary = "获取标签数量", description = "返回标签总数")
    public ResponseEntity<Long> getTagCount() {
        long count = tagService.count();
        return ResponseEntity.ok(count);
    }
}
