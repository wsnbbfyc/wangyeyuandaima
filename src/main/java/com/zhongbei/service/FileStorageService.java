package com.zhongbei.service;

import com.zhongbei.model.FileResource;
import com.zhongbei.repository.FileResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileStorageService {
    
    @Autowired
    private FileResourceRepository fileResourceRepository;
    
    private final String uploadDir = System.getProperty("user.dir") + "/uploads";
    
    public List<FileResource> findAll() {
        return fileResourceRepository.findAllByOrderByUploadTimeDesc();
    }
    
    public Optional<FileResource> findById(Long id) {
        return fileResourceRepository.findById(id);
    }
    
    public FileResource saveFile(MultipartFile file, String description) throws IOException {
        // 创建上传目录
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 生成唯一文件名
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;
        
        // 保存文件
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // 创建文件记录
        FileResource fileResource = new FileResource();
        fileResource.setFileName(fileName);
        fileResource.setOriginalName(originalName);
        fileResource.setFileType(file.getContentType());
        fileResource.setFileSize(file.getSize());
        fileResource.setDescription(description);
        
        return fileResourceRepository.save(fileResource);
    }
    
    public Path getFilePath(String fileName) {
        return Paths.get(uploadDir).resolve(fileName);
    }
    
    public void deleteById(Long id) {
        fileResourceRepository.deleteById(id);
    }
    
    public long count() {
        return fileResourceRepository.count();
    }
}
