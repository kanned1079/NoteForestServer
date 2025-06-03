package com.example.noteforestserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/public/images/**") // 对外暴露的 URL 前缀
                .addResourceLocations("file:/Users/kanna/IdeaProjects/avatars"); // 本地目录（注意 file: 前缀）
    }
}
