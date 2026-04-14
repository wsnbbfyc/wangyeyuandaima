package com.zhongbei.service;

import com.zhongbei.model.User;
import com.zhongbei.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User register(String username, String password, String email, String nickname) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setNickname(nickname != null && !nickname.isEmpty() ? nickname : username);
        
        return userRepository.save(user);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public void updateLastLoginTime(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLoginTime(LocalDateTime.now());
            userRepository.save(user);
        }
    }
    
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public void updatePassword(Long userId, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new RuntimeException("用户不存在");
        }
    }
    
    public void addFavorite(Long userId, com.zhongbei.model.Diary diary) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.getFavorites().add(diary);
            userRepository.save(user);
        }
    }
    
    public void removeFavorite(Long userId, com.zhongbei.model.Diary diary) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.getFavorites().remove(diary);
            userRepository.save(user);
        }
    }
    
    public boolean isFavorite(Long userId, Long diaryId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return userOpt.get().getFavorites().stream().anyMatch(d -> d.getId().equals(diaryId));
        }
        return false;
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
