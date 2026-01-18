package com.example.libapi.controller;

import com.example.libapi.dto.BookDto;
import com.example.libapi.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest

@WebMvcTest(BookController.class)
@AutoConfigureRestTestClient
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

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
}