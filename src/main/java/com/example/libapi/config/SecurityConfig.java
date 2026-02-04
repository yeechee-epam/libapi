package com.example.libapi.config;

import com.example.libapi.config.security.AuthenticationErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
//1/26-2 bcos there is a conflict btw security config n main n testsecurityconfig class in test
@Profile("!test")
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthenticationErrorHandler authenticationErrorHandler;

    @Bean
    public SecurityFilterChain httpSecurity(final HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authz -> authz
                        // Protect these endpoints
                        .requestMatchers("/api/messages/protected", "/api/messages/admin").authenticated()
                        // Allow public access to /books and /books/{id}
//                        .requestMatchers("/books", "/books/**").permitAll()
                        // All other endpoints are public
                        .anyRequest().permitAll())
                .cors(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(authenticationErrorHandler))
                .build();
    }
}