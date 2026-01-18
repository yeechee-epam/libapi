package com.example.libapi.service;

import com.example.libapi.dto.AuthorWithBooksPageDto;
import com.example.libapi.entity.Author;
import com.example.libapi.entity.Book;
import com.example.libapi.exception.ResourceNotFoundException;
import com.example.libapi.mapper.AuthorMapper;
import com.example.libapi.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorServiceTest {

    private final AuthorRepository authorRepository = mock(AuthorRepository.class);
    private final AuthorMapper authorMapper = mock(AuthorMapper.class);
    private final AuthorService authorService = new AuthorService(authorRepository, authorMapper);

    @Test
    void getAuthorWithBooksPage_returnsAuthorWithBooksAndLinks() {
        // Arrange
        Author author = new Author();
        author.setId(1L);
        author.setName("Author Name");

        Book book1 = new Book();
        book1.setId(10L);
        book1.setName("Book One");
        Book book2 = new Book();
        book2.setId(11L);
        book2.setName("Book Two");

        author.setBooks(Arrays.asList(book1, book2));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        // Act
        AuthorWithBooksPageDto dto = authorService.getAuthorWithBooksPage(1L, PageRequest.of(0, 10));

        // Assert
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Author Name");
        assertThat(dto.getBooks().getContent()).hasSize(2);
        assertThat(dto.getBooks().getContent().get(0).getBookLink()).isEqualTo("/books/10");
        assertThat(dto.getBooks().getContent().get(1).getBookLink()).isEqualTo("/books/11");
    }

    @Test
    void getAuthorWithBooksPage_throwsResourceNotFoundException() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authorService.getAuthorWithBooksPage(99L, PageRequest.of(0, 10)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Author not found with id: 99");
    }
}