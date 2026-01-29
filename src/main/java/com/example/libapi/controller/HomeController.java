package com.example.libapi.controller;

import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;
@RestController
public class HomeController {
    @GetMapping("/userinfo")
//    public String home(@AuthenticationPrincipal OAuth2User user) {
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        var authorities = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
//
//        return "Hello, " + user.getName() + "!<br/><br/>Authorities: " + authorities;
//    }
    public Map<String,Object> userInfo(@AuthenticationPrincipal Jwt jwt)
    {
        return jwt.getClaims();
    }
}
