package com.example.libapi.config;

//package com.example.libapi;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.core.convert.converter.Converter;
//import tools.jackson.databind.ObjectMapper;

//1/26-2 bcos there is a conflict btw security config n main n testsecurityconfig class in test
import com.fasterxml.jackson.databind.ObjectMapper;
@Profile("test")
@TestConfiguration
public class TestSecurityConfig {
    static final String AUTH0_TOKEN = "token";
    static final String SUB = "sub";
    static final String AUTH0ID = "sms|12345678";

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> {

            if ("admin-token".equals(token)) {
                return buildJwt(List.of("admin"));
            }

            if ("no-role-token".equals(token)) {
                return buildJwt(List.of());//authenticated, no role
            }

//            throw new JwtException("Invalid token");
            throw new BadJwtException("Invalid token");
        };
    }

    private Jwt buildJwt(List<String> roles) {
        Map<String, Object> claims = Map.of(
                "sub", "test-user",
                "https://spring-boot.example.com/roles", roles
        );

        return new Jwt(
                "mock-token",
                Instant.now(),
                Instant.now().plusSeconds(300),
                Map.of("alg", "none"),
                claims
        );

    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
//        return http.build();
//    }
@Bean
public ObjectMapper objectMapper() {
    return new ObjectMapper();
}









}

//mock the jwt decoder
//currently, extracts admin token, call mocked decoder, get a jwt object, run converter, & create JwtAuthenticationToken, store in secutirycontext, evaluate @preauthorize(hasrole))
