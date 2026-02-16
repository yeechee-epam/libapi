package com.example.libapi.config;

import com.example.libapi.config.security.AuthenticationErrorHandler;
import com.example.libapi.config.security.AuthorizationErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
//1/26-2 bcos there is a conflict btw security config n main n testsecurityconfig class in test
//@Profile("!test")
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthenticationErrorHandler authenticationErrorHandler;

//    skipped requestMatchers("/admin") bcos @PreAuthorize already protects
    @Bean
    public SecurityFilterChain httpSecurity(final HttpSecurity http, AuthorizationErrorHandler authorizationErrorHandler) throws Exception {
        return http
                .csrf(csrf-> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        // Protect these endpoints
                        .requestMatchers("/admin/**").hasRole("admin")
//                        .requestMatchers(HttpMethod.GET, "/books/me/**").authenticated()
                        .requestMatchers(HttpMethod.POST,"/books").hasRole("admin")
                        // Allow public access to /books and /books/{id}
                        .requestMatchers(HttpMethod.GET, "/books").permitAll()
                        .requestMatchers(HttpMethod.GET, "/books/**").permitAll()
                        // All other endpoints are public
//                        .requestMatchers(HttpMethod.POST,"/books").hasRole("admin")
                        .anyRequest().permitAll())
                .cors(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2
//                        .jwt(Customizer.withDefaults())
//                        .jwtAuthenticationConverter
                        .jwt(jwt->jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(authenticationErrorHandler)
                        .accessDeniedHandler(authorizationErrorHandler)
                )

                .build();
    }
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter()
    {
        JwtGrantedAuthoritiesConverter converter=new JwtGrantedAuthoritiesConverter();
//        grantedAuthoritiesConverter.setJwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("https://spring-boot.example.com/roles");
        converter.setAuthorityPrefix("ROLE_");//Spring prefix for hasRole

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtConverter;
    }

}