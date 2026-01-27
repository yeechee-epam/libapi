package com.example.libapi.config;

//package com.example.hello.config;

//package com.example.helloworld.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;
//global web configuration class
//configure CORS for backend (when ur frontend eg Angular app is served from dif origin (domain/port) than backend
//tells spring boot to override web context of controller classes (bcos this class implements WebMvcConfigurer) & all @Controller classes are registered in this web context
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig implements WebMvcConfigurer {

    private final ApplicationProperties applicationProps;

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
//                .allowedOrigins(applicationProps.getClientOriginUrl())
                .allowedOrigins("*")
                .allowedHeaders(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE)
                .allowedMethods(HttpMethod.GET.name())
                .maxAge(86400);
        System.out.println("CORS allowed origin: " + applicationProps.getClientOriginUrl());
    }
}

