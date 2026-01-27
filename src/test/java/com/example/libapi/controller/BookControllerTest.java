package com.example.libapi.controller;

import com.example.libapi.config.ApplicationProperties;
import com.example.libapi.config.TestSecurityConfig;
import com.example.libapi.dto.BookDto;
import com.example.libapi.mapper.BookMapper;
import com.example.libapi.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
@WebMvcTest(BookController.class)
@AutoConfigureRestTestClient
@org.springframework.test.context.TestPropertySource(properties = {

//1/26-to disable all oauth2
"spring.autoconfigure.exclude=org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration,org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration,org.springframework.boot.security.oauth2.client.autoconfigure.reactive.ReactiveOAuth2ClientAutoConfiguration,org.springframework.boot.security.oauth2.client.autoconfigure.reactive.ReactiveOAuth2ClientWebSecurityAutoConfiguration,org.springframework.boot.security.oauth2.client.autoconfigure.reactive.ReactiveOAuth2ClientWebSecurityAutoConfiguration,org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration,org.springframework.boot.security.oauth2.server.resource.autoconfigure.reactive.ReactiveOAuth2ResourceServerAutoConfiguration"
})
//1/26-2 bcos there is a conflict btw security config n main n testsecurityconfig class in test
@ActiveProfiles({"test"})//no need; test container will inject db config

//@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private BookMapper bookMapper;
    //1/27
    @MockitoBean
    private ApplicationProperties applicationProperties;

    @Test
    @DisplayName("GET /books returns paginated list of books")
    void testListBooks() throws Exception {
        // Arrange
        BookDto bookDto = new BookDto();
        bookDto.setName("Book Title");
        bookDto.setAuthorId(1L);
        bookDto.setAuthorName("Author Name");

        Page<BookDto> bookPage = new PageImpl<>(List.of(bookDto), PageRequest.of(0, 10), 1);

        Mockito.when(bookService.list(any(Pageable.class))).thenReturn(bookPage);

        // Act & Assert
        mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())//test for HTTP 200 code
                .andExpect(jsonPath("$.content[0].name").value("Book Title"))
                .andExpect(jsonPath("$.content[0].authorId").value(1))
                .andExpect(jsonPath("$.content[0].authorName").value("Author Name"))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));

    }
    @Test
    @DisplayName("GET /books with negative page returns 400 Bad Request")
    void testListBooksBadRequest() throws Exception {
        mockMvc.perform(get("/books")
                        .param("page", "-1") // Invalid negative page
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /books/{id} returns book with author info and link")
    void testGetBookByIdReturnsBookWithAuthorInfoAndLink() throws Exception {
        // Arrange
        BookDto bookDto = new BookDto();
        bookDto.setId(10L);
        bookDto.setName("Book Title");
        bookDto.setAuthorId(2L);
        bookDto.setAuthorName("Author Name");
        bookDto.setAuthorLink("/authors/2");

        Mockito.when(bookService.getBookById(10L)).thenReturn(Optional.of(bookDto));

        // Act & Assert
        mockMvc.perform(get("/books/10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Book Title"))
                .andExpect(jsonPath("$.authorId").value(2))
                .andExpect(jsonPath("$.authorName").value("Author Name"))
                .andExpect(jsonPath("$.authorLink").value("/authors/2"));
    }

    @Test
    @DisplayName("GET /books/{id} returns 404 for non-existent book")
    void testGetBookByIdReturns404() throws Exception {
        Mockito.when(bookService.getBookById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/books/99999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}