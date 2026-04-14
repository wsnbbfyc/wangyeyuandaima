package com.zhongbei.service;

import com.zhongbei.model.SiteSetting;
import com.zhongbei.repository.SiteSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class SiteSettingService {
    
    @Autowired
    private SiteSettingRepository siteSettingRepository;
    
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/backgrounds";
    
    public Optional<SiteSetting> findByKey(String key) {
        return siteSettingRepository.findBySettingKey(key);
    }
    
    public String getValue(String key, String defaultValue) {
        return findByKey(key).map(SiteSetting::getSettingValue).orElse(defaultValue);
    }
    
    public SiteSetting saveSetting(String key, String value, String description) {
        SiteSetting setting = findByKey(key).orElse(new SiteSetting());
        setting.setSettingKey(key);
        setting.setSettingValue(value);
        if (description != null) {
            setting.setDescription(description);
        }
        return siteSettingRepository.save(setting);
    }
    
    public String saveBackgroundImage(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = "background_" + UUID.randomUUID() + extension;
        
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return "/uploads/backgrounds/" + fileName;
    }
}
