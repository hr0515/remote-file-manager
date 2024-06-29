package com.lhr.manager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



/**
 * @description: 对引入的 mjs 静态资源 设置正确格式
 * @author: LHR
 * @date: 2024-06-29 14:52
 **/
@Configuration
public class MJSConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.mediaType("mjs", MediaType.valueOf("application/javascript"));
    }
}