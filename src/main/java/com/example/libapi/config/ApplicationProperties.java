package com.example.libapi.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Data
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    String clientOriginUrl;

//    @ConstructorBinding
//    public ApplicationProperties(final String clientOriginUrl) {
//        this.clientOriginUrl = clientOriginUrl;
//    }

}
