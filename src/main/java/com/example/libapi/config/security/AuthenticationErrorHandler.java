package com.example.libapi.config.security;

//package com.example.hello.config.security;

//package com.example.helloworld.config.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.example.libapi.entity.ErrorMessage;
//import com.fasterxml.jackson.databind.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
//import tools.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class AuthenticationErrorHandler implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;

    @Override
    public void commence(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException authException
    ) throws IOException, ServletException {
//        final var errorMessage = ErrorMessage.from("Requires authentication");
//        final var json = mapper.writeValueAsString(errorMessage);
//
//        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.getWriter().write(json);
//        response.flushBuffer();

//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"Unauthorized\"}");
        response.flushBuffer();
    }


}

