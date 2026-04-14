package com.zhongbei.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhongbei.service.DiaryService;
import com.zhongbei.service.FileStorageService;
import com.zhongbei.service.SiteSettingService;
import com.zhongbei.service.TagService;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/chat")
public class ChatController {
    
    @Autowired
    private SiteSettingService siteSettingService;
    
    @Autowired
    private DiaryService diaryService;
    
    @Autowired
    private TagService tagService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Value("${openai.api.key:}")
    private String openAiApiKey;
    
    @Value("${deepseek.api.key:}")
    private String deepseekApiKey;
    
    @Value("${ai.provider:local}")
    private String aiProvider;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();
    
    private List<Map<String, String>> conversationHistory = new ArrayList<>();
    
    @GetMapping
    public String chat(Model model) {
        model.addAttribute("themePrimaryColor", siteSettingService.getValue("background_color", "#667eea"));
        model.addAttribute("themeBackgroundImage", siteSettingService.getValue("background_image", ""));
        model.addAttribute("messages", conversationHistory);
        model.addAttribute("aiProvider", aiProvider);
        return "chat/index";
    }
    
    @PostMapping("/send")
    public String sendMessage(@RequestParam("message") String message, Model model) {
        if (message == null || message.trim().isEmpty()) {
            return "redirect:/chat";
        }
        
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", message);
        conversationHistory.add(userMsg);
        
        String response;
        try {
            response = getAIResponse(message);
        } catch (Exception e) {
            response = "抱歉，AI服务暂时不可用: " + e.getMessage();
        }
        
        Map<String, String> aiMsg = new HashMap<>();
        aiMsg.put("role", "ai");
        aiMsg.put("content", response);
        conversationHistory.add(aiMsg);
        
        if (conversationHistory.size() > 50) {
            conversationHistory = conversationHistory.subList(conversationHistory.size() - 50, conversationHistory.size());
        }
        
        model.addAttribute("themePrimaryColor", siteSettingService.getValue("background_color", "#667eea"));
        model.addAttribute("themeBackgroundImage", siteSettingService.getValue("background_image", ""));
        model.addAttribute("messages", conversationHistory);
        model.addAttribute("aiProvider", aiProvider);
        return "chat/index";
    }
    
    @PostMapping("/clear")
    public String clearChat() {
        conversationHistory.clear();
        return "redirect:/chat";
    }
    
    @PostMapping("/provider")
    public String setProvider(@RequestParam("provider") String provider) {
        aiProvider = provider;
        conversationHistory.clear();
        return "redirect:/chat";
    }
    
    private String getAIResponse(String message) throws IOException {
        System.out.println("=== AI Provider: " + aiProvider + " ===");
        System.out.println("=== DeepSeek Key configured: " + (deepseekApiKey != null && !deepseekApiKey.isEmpty()) + " ===");
        
        switch (aiProvider) {
            case "openai":
                return callOpenAI(message);
            case "deepseek":
                System.out.println("Calling DeepSeek API...");
                return callDeepSeek(message);
            case "claude":
                return callClaude(message);
            default:
                return getLocalResponse(message);
        }
    }
    
    private String callDeepSeek(String message) throws IOException {
        System.out.println("DeepSeek API Key: " + (deepseekApiKey != null ? "***" + deepseekApiKey.substring(Math.max(0, deepseekApiKey.length()-5)) : "null"));
        
        if (deepseekApiKey == null || deepseekApiKey.isEmpty()) {
            return "请配置 DeepSeek API Key!\n\n" +
                   "在 application.properties 中添加:\n" +
                   "deepseek.api.key=your-api-key\n\n" +
                   "申请地址: https://platform.deepseek.com";
        }
        
        System.out.println("Sending request to DeepSeek...");
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("max_tokens", 1000);
        requestBody.put("temperature", 0.7);
        
        List<Map<String, String>> messages = new ArrayList<>();
        
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", "你是一个友好的AI助手，请用中文回答用户的问题。简洁明了。");
        messages.add(systemMsg);
        
        for (Map<String, String> msg : conversationHistory) {
            if (msg.get("content") != null && msg.get("content").length() < 500) {
                Map<String, String> msgCopy = new HashMap<>();
                msgCopy.put("role", "user".equals(msg.get("role")) ? "user" : "assistant");
                msgCopy.put("content", msg.get("content"));
                messages.add(msgCopy);
            }
        }
        
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", message);
        messages.add(userMsg);
        
        requestBody.put("messages", messages);
        
        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                objectMapper.writeValueAsString(requestBody),
                MediaType.parse("application/json")
        );
        
        Request request = new Request.Builder()
                .url("https://api.deepseek.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + deepseekApiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            
            if (!response.isSuccessful()) {
                return "DeepSeek API 错误 " + response.code() + ": " + responseBody;
            }
            
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode choices = rootNode.path("choices");
            
            if (choices.isArray() && choices.size() > 0) {
                return choices.get(0).path("message").path("content").asText();
            }
            
            return "无法获取AI回复";
        }
    }
    
    private String callOpenAI(String message) throws IOException {
        if (openAiApiKey == null || openAiApiKey.isEmpty()) {
            return "请配置 OpenAI API Key!\n\n" +
                   "在 application.properties 中添加:\n" +
                   "openai.api.key=your-api-key";
        }
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        
        List<Map<String, String>> messages = new ArrayList<>();
        
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", "你是一个友好的AI助手，请用中文回答用户的问题。");
        messages.add(systemMsg);
        
        for (Map<String, String> msg : conversationHistory) {
            if (msg.get("content").length() < 500) {
                messages.add(msg);
            }
        }
        
        requestBody.put("messages", messages);
        
        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                objectMapper.writeValueAsString(requestBody),
                MediaType.parse("application/json")
        );
        
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + openAiApiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "OpenAI API 错误: " + response.code();
            }
            
            String responseBody = response.body().string();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode choices = rootNode.path("choices");
            
            if (choices.isArray() && choices.size() > 0) {
                return choices.get(0).path("message").path("content").asText();
            }
            
            return "无法获取AI回复";
        }
    }
    
    private String callClaude(String message) throws IOException {
        return "Claude API 集成开发中...\n\n" +
               "要接入 Claude API，请等待更新。";
    }
    
    private String getLocalResponse(String message) {
        message = message.toLowerCase();
        
        if (message.contains("你好") || message.contains("hello") || message.contains("hi")) {
            return "你好！我是你的AI助手，有什么可以帮你的吗？";
        }
        
        if (message.contains("帮助") || message.contains("help")) {
            return "我可以帮你：\n" +
                   "• 回答问题\n" +
                   "• 聊天解闷\n" +
                   "• 提供建议\n\n" +
                   "当前模式：" + getProviderName() + "\n\n" +
                   "如需接入更强的AI，请配置 API Key:\n" +
                   "• DeepSeek (推荐): deepseek.api.key\n" +
                   "• OpenAI: openai.api.key";
        }
        
        if (message.contains("随笔") || message.contains("日记")) {
            long count = diaryService.count();
            return "你现在有 " + count + " 篇随笔记录！";
        }
        
        if (message.contains("标签")) {
            long count = tagService.count();
            return "你现在有 " + count + " 个标签！";
        }
        
        if (message.contains("文件") || message.contains("资料")) {
            long count = fileStorageService.count();
            return "你现在上传了 " + count + " 个文件！";
        }
        
        if (message.contains("今天") || message.contains("天气")) {
            Random random = new Random();
            String[] moods = {"开心", "平静", "忙碌", "充实"};
            String mood = moods[random.nextInt(moods.length)];
            return "今天也要保持" + mood + "的心情！💪";
        }
        
        if (message.contains("谢谢") || message.contains("感谢")) {
            return "不客气！很高兴能帮到你 😊";
        }
        
        if (message.contains("再见") || message.contains("bye")) {
            return "再见！有空再聊～ 👋";
        }
        
        if (message.contains("api") || message.contains("key")) {
            return "配置 AI API Key 的方法：\n\n" +
                   "1. 打开 application.properties\n" +
                   "2. 添加: openai.api.key=你的key\n" +
                   "3. 重启应用";
        }
        
        String[] responses = {
            "我明白了！",
            "嗯嗯，听起来不错！",
            "我理解你的意思了～",
            "很有意思的话题！",
            "你可以换个话题试试？",
            "这方面我还可以帮你很多！",
            "真的吗？那太棒了！",
            "我随时在这里陪你聊天～"
        };
        
        Random random = new Random();
        return responses[random.nextInt(responses.length)];
    }
    
    private String getProviderName() {
        switch (aiProvider) {
            case "openai":
                return "OpenAI GPT";
            case "deepseek":
                return "DeepSeek";
            default:
                return "本地AI";
        }
    }
}
