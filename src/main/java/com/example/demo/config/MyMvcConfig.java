//package com.example.demo.config;
//
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@EnableAutoConfiguration
//@Configuration
//public class MyMvcConfig implements WebMvcConfigurer{
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry){
//        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*")
//                .allowedHeaders("Content-Type", "X-Requested-With", "accept",
//                        "Origin", "Access-Control-Request-Method",
//                        "Access-Control-Request-Headers").
//                exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
//                .allowCredentials(true);
//    }
//}
