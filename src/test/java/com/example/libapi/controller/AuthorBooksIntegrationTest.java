package com.example.libapi.controller;

import com.example.libapi.LibapiApplication;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//1/26-2 bcos there is a conflict btw security config n main n testsecurityconfig class in test
@ActiveProfiles({"test"})//no need; test container will inject db config
//1/26-disable security issues at endpoint (disable auth0)
@Import(TestSecurityConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
//        ,
//properties = {
//        "spring.autoconfigure.exclude=org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration,org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration,org.springframework.boot.security.oauth2.client.autoconfigure.reactive.ReactiveOAuth2ClientAutoConfiguration,org.springframework.boot.security.oauth2.client.autoconfigure.reactive.ReactiveOAuth2ClientWebSecurityAutoConfiguration,org.springframework.boot.security.oauth2.client.autoconfigure.reactive.ReactiveOAuth2ClientWebSecurityAutoConfiguration,org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration,org.springframework.boot.security.oauth2.server.resource.autoconfigure.reactive.ReactiveOAuth2ResourceServerAutoConfiguration"
//}
//        ,classes = {LibapiApplication.class, TestSecurityConfig.class}
)
@Testcontainers

//@AutoConfigureMockMvc(addFilters = false)
//@WebAppConfiguration
//@Profile("integration-test")
@AutoConfigureRestTestClient //for RestTestClient
class AuthorBooksIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?>postgreSQLContainer=
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

    /*
    *  @BeforeEach
  void setUp() {
    RestAssured.baseURI = "http://localhost:" + port;
    customerRepository.deleteAll();
  }
  * not needed because of @RestTestClient as Spring Boot automatically inject port & configure client
    * */

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