package com.example.demo.config;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * 配置项目对mvc模式的支持和静态资源
 */
@Configuration
@EnableAutoConfiguration
public class MvcConfig extends WebMvcConfigurationSupport{

    // 静态资源文件位置配置
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

}

