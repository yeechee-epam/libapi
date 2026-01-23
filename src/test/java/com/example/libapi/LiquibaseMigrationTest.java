package com.example.libapi;

import com.example.libapi.entity.Book;
import com.example.libapi.repository.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LiquibaseMigrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres")
                    .withDatabaseName("libapidb_test")
                    .withUsername("admin")
                    .withPassword("root");

    @Autowired

    private BookRepository bookRepository;

//    test that liquibase migration to test db container  was successful by checking if book exists using existing migration data without using @BeforeEach or @BeforeAll to insert books
    @Test
    @DisplayName("Test find book by id from liquibase migration")
    void find_book_by_id_from_liquibase() {
        Optional<Book> bookOptional = bookRepository.findById(1L);
        Assertions.assertThat(bookOptional).isPresent()
                .get()
                .extracting(Book::getName)
                .isEqualTo("Romeo and Juliet");
    }
}