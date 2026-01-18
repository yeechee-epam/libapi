package com.example.libapi.controller;

import com.example.libapi.entity.Author;
import com.example.libapi.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
//import org.springframework.boot.resttestclient.RestTestClient;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles({"test"})
@SpringBootTest
@AutoConfigureRestTestClient
class AuthorControllerIntegrationTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private RestTestClient restTestClient;

    @BeforeEach
    void setUp() {
        authorRepository.deleteAll();
        authorRepository.save(Author.builder().name("Author One").build());
        authorRepository.save(Author.builder().name("Author Two").build());
    }

    @Test
    void testListAuthorsReturns200() {
        restTestClient.get().uri("/authors?page=0&size=10")
                .exchange()
                .expectStatus().isOk()//test for http 200 code
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body).contains("Author One");
                    assertThat(body).contains("Author Two");
                });
    }
}