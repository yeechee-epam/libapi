package com.example.libapi.controller;

import com.example.libapi.config.TestSecurityConfig;
import com.example.libapi.entity.Author;
import com.example.libapi.entity.Book;
import com.example.libapi.repository.AuthorRepository;
import com.example.libapi.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
//import org.springframework.boot.resttestclient.RestTestClient;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureRestTestClient
//1/26-2 bcos there is a conflict btw security config n main n testsecurityconfig class in test
@ActiveProfiles({"test"})//no need; test container will inject db config
//1/26-disable security issues at endpoint (disable auth0)
@Import(TestSecurityConfig.class)

class AuthorControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer=
            new PostgreSQLContainer<>("postgres")
                    .withDatabaseName("libapidb_test")
                    .withUsername("admin")
                    .withPassword("root");


    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RestTestClient restTestClient;
    //    1/26-mocking oauth2's ClientRegistrationRepository client bean to avoid authentication issue
    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    private Long authorId;
    private Long book1Id;
    private Long book2Id;

    @BeforeEach
    void setUp() {

        bookRepository.deleteAll();//delete books first to avoid constraint with deleting author when their book exist
        authorRepository.deleteAll();
//        authorRepository.save(Author.builder().name("Author One").build());
//        authorRepository.save(Author.builder().name("Author Two").build());
        Author author = authorRepository.save(Author.builder().name("Author One").build());
        authorId = author.getId();

        Book book1 = bookRepository.save(Book.builder().name("Book A").author(author).build());
        Book book2 = bookRepository.save(Book.builder().name("Book B").author(author).build());
        book1Id = book1.getId();
        book2Id = book2.getId();

        authorRepository.save(Author.builder().name("Author Two").build());
    }

    @Test
    void testGetAuthorByIdReturns200AndBooksWithLinks() {
        String responseBody = restTestClient.get().uri("/authors/" + authorId + "?page=0&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).contains("Author One");
        assertThat(responseBody).contains("Book A");
        assertThat(responseBody).contains("Book B");
        assertThat(responseBody).contains("\"bookLink\":\"/books/" + book1Id + "\"");
        assertThat(responseBody).contains("\"bookLink\":\"/books/" + book2Id + "\"");
    }

    @Test
    void testGetAuthorByIdReturns404ForNonExistentAuthor() {
        restTestClient.get().uri("/authors/99999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Author not found with id: 99999"));
    }

    @Test
    void testGetAuthorByIdReturns400ForInvalidId() {
        restTestClient.get().uri("/authors/abc")
                .exchange()
                .expectStatus().isBadRequest();
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

    @Test
    void testCreateAuthorReturns201AndDetails() {
        String requestBody = """
            {
                "name": "Integration Author"
            }
            """;
        String responseBody = restTestClient.post().uri("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).contains("Integration Author");
        assertThat(responseBody).contains("\"id\":");
    }

    @Test
    void testCreateAuthorReturns400ForInvalidName() {
        String requestBody = """
            {
                "name": ""
            }
            """;
        restTestClient.post().uri("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Author name is required"));
    }

    @Test
    void testCreateAuthorReturns409ForDuplicate() {
        authorRepository.save(Author.builder().name("Dup Author").build());
        String requestBody = """
            {
                "name": "Dup Author"
            }
            """;
        restTestClient.post().uri("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Author with this name already exists"));
    }



    @Test
    void testDeleteAuthorReturns204() {
        Author authorWithNoBook = authorRepository.save(Author.builder().name("Author with no book").build());
        restTestClient.delete().uri("/authors/" + authorWithNoBook.getId())
                .exchange()
                .expectStatus().isNoContent();

        assertThat(authorRepository.findById(authorWithNoBook.getId())).isEmpty();
    }

    @Test
    void testDeleteAuthorReturns404ForNonExistentAuthor() {
        restTestClient.delete().uri("/authors/99999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Author not found with id: 99999"));
    }

    @Test
    void testDeleteAuthorReturns400ForInvalidId() {
        restTestClient.delete().uri("/authors/abc")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testDeleteAuthorReturns409IfHasBooks() {
        Author author = authorRepository.save(Author.builder().name("Author With Book").build());
        bookRepository.save(Book.builder().name("Book by Author").author(author).build());

        restTestClient.delete().uri("/authors/" + author.getId())
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Cannot delete author with id: " + author.getId()));
    }
}