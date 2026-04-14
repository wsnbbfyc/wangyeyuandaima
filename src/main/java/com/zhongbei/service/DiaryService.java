package com.zhongbei.service;

import com.zhongbei.model.Diary;
import com.zhongbei.model.Tag;
import com.zhongbei.model.User;
import com.zhongbei.repository.DiaryRepository;
import com.zhongbei.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DiaryService {
    
    @Autowired
    private DiaryRepository diaryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Diary> findAll() {
        return diaryRepository.findAllByOrderByPinnedAndCreateTimeDesc();
    }
    
    public Optional<Diary> findById(Long id) {
        return diaryRepository.findById(id);
    }
    
    public Diary save(Diary diary) {
        return diaryRepository.save(diary);
    }
    
    public void deleteById(Long id) {
        Optional<Diary> diaryOpt = diaryRepository.findById(id);
        if (diaryOpt.isPresent()) {
            Diary diary = diaryOpt.get();
            diary.setIsDeleted(true);
            diaryRepository.save(diary);
        }
    }
    
    public void restoreById(Long id) {
        Optional<Diary> diaryOpt = diaryRepository.findById(id);
        if (diaryOpt.isPresent()) {
            Diary diary = diaryOpt.get();
            diary.setIsDeleted(false);
            diaryRepository.save(diary);
        }
    }
    
    public void permanentlyDeleteById(Long id) {
        diaryRepository.deleteById(id);
    }
    
    public List<Diary> findAllDeleted() {
        return diaryRepository.findAllDeleted();
    }
    
    public void togglePinned(Long id) {
        Optional<Diary> diaryOpt = diaryRepository.findById(id);
        if (diaryOpt.isPresent()) {
            Diary diary = diaryOpt.get();
            diary.setIsPinned(diary.getIsPinned() == null ? true : !diary.getIsPinned());
            diaryRepository.save(diary);
        }
    }
    
    public List<Diary> findByTagId(Long tagId) {
        return diaryRepository.findByTagId(tagId);
    }
    
    public List<Diary> search(String keyword) {
        return diaryRepository.searchByKeyword(keyword);
    }
    
    public long count() {
        return diaryRepository.count();
    }
    
    // API methods
    public void softDelete(Long id) {
        deleteById(id);
    }
    
    public void restore(Long id) {
        restoreById(id);
    }
    
    public void togglePin(Long id) {
        togglePinned(id);
    }
    
    public void toggleFavorite(Long diaryId, String username) {
        Optional<Diary> diaryOpt = diaryRepository.findById(diaryId);
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (diaryOpt.isPresent() && userOpt.isPresent()) {
            Diary diary = diaryOpt.get();
            User user = userOpt.get();
            Set<Diary> favorites = user.getFavorites();
            
            if (favorites.contains(diary)) {
                favorites.remove(diary);
            } else {
                favorites.add(diary);
            }
            userRepository.save(user);
        }
    }
}
