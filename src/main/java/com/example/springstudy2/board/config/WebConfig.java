package com.example.springstudy2.board.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 브라우저에서 /upload/파일명 으로 접근하면
        // 실제 컴퓨터의 uploadPath 경로에 있는 파일을 찾아줌
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:///" + uploadPath);
    }
}