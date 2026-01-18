package com.example.libapi.service;

import com.example.libapi.dto.BookDto;
import com.example.libapi.entity.Author;
import com.example.libapi.entity.Book;
import com.example.libapi.mapper.BookMapper;
import com.example.libapi.repository.AuthorRepository;
import com.example.libapi.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {
    private final BookRepository bookRepository = mock(BookRepository.class);
    private final BookMapper bookMapper = mock(BookMapper.class);
    private final AuthorRepository authorRepository = mock(AuthorRepository.class);

    private final BookService bookService = new BookService(bookRepository, authorRepository,bookMapper);

    @Test
    void getBookById_returnsBookDtoWithAuthorLink() {
        Book book = new Book();
        book.setId(10L);
        book.setName("Book Title");
        Author author = new Author();
        author.setId(1L);
        author.setName("Author Name");
        book.setAuthor(author);

        BookDto bookDto = new BookDto();
        bookDto.setId(10L);
        bookDto.setName("Book Title");
        bookDto.setAuthorId(1L);
        bookDto.setAuthorName("Author Name");

        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Optional<BookDto> result = bookService.getBookById(10L);

        assertThat(result).isPresent();
        assertThat(result.get().getAuthorLink()).isEqualTo("/authors/1");
    }

    @Test
    void getBookById_returnsEmptyIfNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<BookDto> result = bookService.getBookById(99L);
        assertThat(result).isEmpty();
    }
    @Test
    void testListBooks() {
        BookRepository bookRepository = mock(BookRepository.class);
        BookMapper bookMapper = mock(BookMapper.class);

        BookService bookService = new BookService(bookRepository,authorRepository ,bookMapper);

        Author author = Author.builder().id(1L).name("Author Name").build();
        Book book = Book.builder().id(10L).name("Book Title").author(author).build();
        BookDto dto = new BookDto();
        dto.setName("Book Title");
        dto.setAuthorId(1L);
        dto.setAuthorName("Author Name");

        Page<Book> bookPage = new PageImpl<>(List.of(book));
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(dto);

        Page<BookDto> result = bookService.list(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Book Title", result.getContent().get(0).getName());
        assertEquals(1L, result.getContent().get(0).getAuthorId());
    }
}