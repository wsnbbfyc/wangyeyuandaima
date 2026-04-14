package com.zhongbei.controller;

import com.zhongbei.model.FileResource;
import com.zhongbei.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/files")
public class FileController {
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @GetMapping
    public String listFiles(Model model) {
        model.addAttribute("files", fileStorageService.findAll());
        return "file/list";
    }
    
    @GetMapping("/upload")
    public String uploadForm(Model model) {
        model.addAttribute("fileResource", new FileResource());
        return "file/upload";
    }
    
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                           @RequestParam(value = "description", required = false) String description,
                           RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "请选择要上传的文件");
                return "redirect:/files/upload";
            }
            
            fileStorageService.saveFile(file, description);
            redirectAttributes.addFlashAttribute("success", "文件上传成功！");
            return "redirect:/files";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "文件上传失败：" + e.getMessage());
            return "redirect:/files/upload";
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws MalformedURLException {
        FileResource fileResource = fileStorageService.findById(id)
                .orElseThrow(() -> new RuntimeException("文件不存在"));
        
        Path filePath = fileStorageService.getFilePath(fileResource.getFileName());
        Resource resource = new UrlResource(filePath.toUri());
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileResource.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getOriginalName() + "\"")
                .body(resource);
    }
    
    @PostMapping("/{id}/delete")
    public String deleteFile(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            fileStorageService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "文件删除成功！");
            return "redirect:/files";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "文件删除失败：" + e.getMessage());
            return "redirect:/files";
        }
    }
}
