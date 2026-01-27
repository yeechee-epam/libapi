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
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.http.MediaType;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//@ActiveProfiles({"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
//1/26-2 bcos there is a conflict btw security config n main n testsecurityconfig class in test
@ActiveProfiles({"test"})//no need; test container will inject db config

@AutoConfigureRestTestClient
//1/26-disable security issues at endpoint (disable auth0)
@Import(TestSecurityConfig.class)

class BookControllerIntegrationTest {
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

    private Long bookAId;
    private Long author1Id;
    private String author1Name;
    private Long bookId;
    private Long authorId;
    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();

        Author baseAuthor = authorRepository.save(Author.builder().name("Base Author").build());
        authorId = baseAuthor.getId();

        Book baseBook = bookRepository.save(Book.builder().name("Base Book").author(baseAuthor).build());
                bookId = baseBook.getId();

        Author author1 = authorRepository.save(Author.builder().name("Author One").build());
        Author author2 = authorRepository.save(Author.builder().name("Author Two").build());
        author1Id = author1.getId();
        author1Name = author1.getName();

        Book bookA = bookRepository.save(Book.builder().name("Book A").author(author1).build());
        bookAId = bookA.getId();
        bookRepository.save(Book.builder().name("Book B").author(author2).build());
    }

    @Test
    void testGetBookByIdReturns200AndAuthorLink() {
        String responseBody = restTestClient.get().uri("/books/" + bookId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).contains("Base Book");
        assertThat(responseBody).contains("\"authorLink\":\"/authors/" + authorId + "\"");
    }

    @Test
    void testGetBookByIdReturns404ForNonExistentBook() {
        restTestClient.get().uri("/books/99999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Book not found with id: 99999"));
    }

    @Test
    void testGetBookByIdReturns400ForInvalidId() {
        restTestClient.get().uri("/books/abc")
                .exchange()
                .expectStatus().isBadRequest();
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

//    @Test
//    void testGetBookByIdReturns404ForNonExistentBook() {
//        restTestClient.get().uri("/books/999999")
//                .exchange()
//                .expectStatus().isNotFound();
//    }
@Test
void testCreateBookWithNewAuthorReturns201() {
    String requestBody = """
            {
                "name": "Integration Book",
                "authorName": "Integration Author"
            }
            """;
    String responseBody = restTestClient.post().uri("/books")
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    assertThat(responseBody).contains("Integration Book");
    assertThat(responseBody).contains("Integration Author");
    assertThat(responseBody).contains("authorLink");
}

    @Test
    void testCreateBookWithExistingAuthorReturns201() {
        Author author = authorRepository.save(Author.builder().name("Existing Author").build());
        String requestBody = """
            {
                "name": "Another Book",
                "authorName": "Existing Author"
            }
            """;
        String responseBody = restTestClient.post().uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).contains("Another Book");
        assertThat(responseBody).contains("Existing Author");
        assertThat(responseBody).contains("authorLink");
    }

    @Test
    void testCreateBookDuplicateReturns409() {
        Author author = authorRepository.save(Author.builder().name("Dup Author").build());
        bookRepository.save(Book.builder().name("Dup Book").author(author).build());

        String requestBody = """
            {
                "name": "Dup Book",
                "authorName": "Dup Author"
            }
            """;
        restTestClient.post().uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    void testCreateBookValidationFails() {
        String requestBody = """
            {
                "name": "",
                "authorName": ""
            }
            """;
        restTestClient.post().uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange()
                .expectStatus().isBadRequest();
    }
    @Test
    void testUpdateBookReturns200AndUpdatedDetails() {
        Author author = authorRepository.save(Author.builder().name("Update Author").build());
        Book book = bookRepository.save(Book.builder().name("Old Book").author(author).build());

        String requestBody = """
        {
            "name": "Updated Book",
            "authorName": "Update Author"
        }
        """;
        String responseBody = restTestClient.put().uri("/books/" + book.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).contains("Updated Book");
        assertThat(responseBody).contains("Update Author");
        assertThat(responseBody).contains("\"authorLink\":\"/authors/" + author.getId() + "\"");
    }

    @Test
    void testUpdateBookReturns404ForNonExistentBook() {
        String requestBody = """
        {
            "name": "Doesn't Matter",
            "authorName": "Any Author"
        }
        """;
        restTestClient.put().uri("/books/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Book not found with id: 99999"));
    }

    @Test
    void testUpdateBookReturns409ForDuplicate() {
        Author author = authorRepository.save(Author.builder().name("Dup Author").build());
        Book book1 = bookRepository.save(Book.builder().name("Book 1").author(author).build());
        Book book2 = bookRepository.save(Book.builder().name("Book 2").author(author).build());

        String requestBody = """
        {
            "name": "Book 1",
            "authorName": "Dup Author"
        }
        """;
        restTestClient.put().uri("/books/" + book2.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    void testUpdateBookReturns400ForValidationError() {
        Author author = authorRepository.save(Author.builder().name("Val Author").build());
        Book book = bookRepository.save(Book.builder().name("Val Book").author(author).build());

        String requestBody = """
        {
            "name": "",
            "authorName": ""
        }
        """;
        restTestClient.put().uri("/books/" + book.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .exchange()
                .expectStatus().isBadRequest();
    }



    @Test
    void testDeleteBookReturns204() {
        restTestClient.delete().uri("/books/" + bookId)
                .exchange()
                .expectStatus().isNoContent();


        assertThat(bookRepository.findById(bookId)).isEmpty();
    }

    @Test
    void testDeleteBookReturns404ForNonExistentBook() {
        restTestClient.delete().uri("/books/99999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Book not found with id: 99999"));
    }

    @Test
    void testDeleteBookReturns400ForInvalidId() {
        restTestClient.delete().uri("/books/abc")
                .exchange()
                .expectStatus().isBadRequest();
    }
}