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
class AuthorBooksIntegrationTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RestTestClient restTestClient;

    private Long authorId;
    private Long book1Id;
    private Long book2Id;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();

        Author author = authorRepository.save(Author.builder().name("Author One").build());
        authorId = author.getId();

        Book book1 = bookRepository.save(Book.builder().name("Book A").author(author).build());
        Book book2 = bookRepository.save(Book.builder().name("Book B").author(author).build());
        book1Id = book1.getId();
        book2Id = book2.getId();
    }
//    http request test
//@Test
//void testGetBooksByAuthorReturns200AndBooks() {
//    restTestClient.get().uri("/authors/1/books?page=0&size=10")
//            .exchange()
//            .expectStatus().isOk();
//}


    @Test
    void testGetBooksByAuthorReturns200AndBooks() {
        String responseBody = restTestClient.get().uri("/authors/" + authorId + "/books?page=0&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).contains("Book A");
        assertThat(responseBody).contains("Book B");
        assertThat(responseBody).contains("\"authorId\":" + authorId);
        assertThat(responseBody).contains("\"authorLink\":\"/authors/" + authorId + "\"");
    }

    @Test
    void testGetBooksByAuthorReturns404ForNonExistentAuthor() {
        restTestClient.get().uri("/authors/99999/books")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Author not found with id: 99999"));
    }

    @Test
    void testGetBooksByAuthorReturns400ForInvalidAuthorId() {
        restTestClient.get().uri("/authors/abc/books")
                .exchange()
                .expectStatus().isBadRequest();
    }
}