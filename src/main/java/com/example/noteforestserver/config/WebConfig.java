package com.example.noteforestserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String currentDir = System.getProperty("user.dir") + "/local/";
        registry.addResourceHandler("/public/images/**") // 对外暴露的 URL 前缀
                .addResourceLocations("file:" + currentDir); // 本地目录（注意 file: 前缀）
    }
}
