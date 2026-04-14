package com.zhongbei.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI personalWebsiteOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("个人博客网站 API")
                        .description("一个功能完整的个人博客系统，包含随笔管理、标签管理、AI聊天等功能")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("开发者")
                                .email("developer@example.com")
                                .url("https://github.com/yourusername"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(Collections.singletonList(
                        new Server()
                                .url("http://localhost:8080")
                                .description("开发环境服务器")
                ));
    }
}
