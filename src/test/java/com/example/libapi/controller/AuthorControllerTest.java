package com.example.libapi.controller;

import com.example.libapi.dto.AuthorDto;
import com.example.libapi.service.AuthorService;
import com.example.libapi.mapper.AuthorMapper;
import com.example.libapi.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private AuthorMapper authorMapper;

    @MockitoBean
    private BookService bookService;



    @Test
    @DisplayName("GET /authors returns paginated list of authors with 200 OK")
    void testListAuthors() throws Exception {
        // Arrange
        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(1L);
        authorDto.setName("Author Name");

        Page<AuthorDto> authorPage = new PageImpl<>(List.of(authorDto), PageRequest.of(0, 10), 1);

        Mockito.when(authorService.list(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
        Mockito.when(authorMapper.toDto(any())).thenReturn(authorDto);

        // Act & Assert
        mockMvc.perform(get("/authors")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // The following checks depend on your actual JSON structure
                .andExpect(jsonPath("$.content").isArray());
    }
}