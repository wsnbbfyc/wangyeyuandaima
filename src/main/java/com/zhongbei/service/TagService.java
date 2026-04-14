package com.zhongbei.service;

import com.zhongbei.model.Tag;
import com.zhongbei.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    
    @Autowired
    private TagRepository tagRepository;
    
    public List<Tag> findAll() {
        return tagRepository.findAllByOrderByNameAsc();
    }
    
    public Tag findById(Long id) {
        return tagRepository.findById(id).orElse(null);
    }
    
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }
    
    public void deleteById(Long id) {
        tagRepository.deleteById(id);
    }
    
    public boolean existsByName(String name) {
        return tagRepository.existsByName(name);
    }
    
    public long count() {
        return tagRepository.count();
    }
}
