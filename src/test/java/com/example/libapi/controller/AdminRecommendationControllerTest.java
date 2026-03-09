package com.example.libapi.controller;

import com.example.libapi.config.TestSecurityConfig;
import com.example.libapi.mapper.BookMapper;
import com.example.libapi.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
properties = {
        "spring.liquibase.enabled=false"
})
@AutoConfigureRestTestClient
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class AdminRecommendationControllerTest {

    @Autowired
    private RestTestClient restTestClient;
    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private BookMapper bookMapper;

//    @Autowired
//    private ObjectMapper mapper;

    @Test
    void shouldReturn200_WhenAdminTokenProvided() {

        when(bookService.getBooksRecommendedByCurrentAdmin())
                .thenReturn(List.of());   // return empty list
        restTestClient.get()
                .uri("/admin/recommendations/books")
                .header("Authorization", "Bearer admin-token")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturn403_WhenAuthenticatedButNoRole() {

        restTestClient.get()
                .uri("/admin/recommendations/books")
                .header("Authorization", "Bearer no-role-token")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void shouldReturn401_WhenInvalidTokenProvided() {

        restTestClient.get()
                .uri("/admin/recommendations/books")
                .header("Authorization", "Bearer invalid-token")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturn401_WhenNoTokenProvided() {

        restTestClient.get()
                .uri("/admin/recommendations/books")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
//no token/invalid token - 401
//valid token, missing role (admin) - 403
