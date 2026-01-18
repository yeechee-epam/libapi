package com.example.libapi.controller;

import com.example.libapi.entity.Author;
import com.example.libapi.entity.Book;
import com.example.libapi.repository.AuthorRepository;
import com.example.libapi.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles({"test"})
@SpringBootTest
@AutoConfigureRestTestClient
class BookControllerIntegrationTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RestTestClient restTestClient;

    private Long bookAId;
    private Long author1Id;
    private String author1Name;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();

        Author author1 = authorRepository.save(Author.builder().name("Author One").build());
        Author author2 = authorRepository.save(Author.builder().name("Author Two").build());
        author1Id = author1.getId();
        author1Name = author1.getName();

        Book bookA = bookRepository.save(Book.builder().name("Book A").author(author1).build());
        bookAId = bookA.getId();
        bookRepository.save(Book.builder().name("Book B").author(author2).build());
    }

    @Test
    void testListBooksReturns200() {
        restTestClient.get().uri("/books?page=0&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body).contains("Book A");
                    assertThat(body).contains("Book B");
                    assertThat(body).contains("Author One");
                    assertThat(body).contains("Author Two");
                });
    }

    @Test
    void testGetBookByIdReturns200AndBookWithAuthorInfoAndLink() {
        String responseBody = restTestClient.get().uri("/books/" + bookAId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).contains("Book A");
        assertThat(responseBody).contains(author1Name);
        assertThat(responseBody).contains("\"authorId\":" + author1Id);
        assertThat(responseBody).contains("\"authorLink\":\"/authors/" + author1Id + "\"");
    }

    @Test
    void testGetBookByIdReturns404ForNonExistentBook() {
        restTestClient.get().uri("/books/999999")
                .exchange()
                .expectStatus().isNotFound();
    }
}