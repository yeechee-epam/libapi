package com.example.libapi.service;

import com.example.libapi.dto.BookDto;
import com.example.libapi.entity.Author;
import com.example.libapi.entity.Book;
import com.example.libapi.exception.ResourceNotFoundException;
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
    void findBooksByAuthorId_returnsBooksPage() {
        Author author = new Author();
        author.setId(1L);
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        Book book = new Book();
        book.setId(10L);
        book.setName("Book Title");
        book.setAuthor(author);

        BookDto bookDto = new BookDto();
        bookDto.setId(10L);
        bookDto.setName("Book Title");
        bookDto.setAuthorId(1L);
        bookDto.setAuthorName("Author Name");
        bookDto.setAuthorLink("/authors/1");

        Page<Book> bookPage = new PageImpl<>(List.of(book), PageRequest.of(0, 10), 1);
        when(bookRepository.findByAuthorId(1L, PageRequest.of(0, 10))).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Page<BookDto> result = bookService.findBooksByAuthorId(1L, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(10L);
        assertThat(result.getContent().get(0).getAuthorLink()).isEqualTo("/authors/1");
    }

    @Test
    void findBooksByAuthorId_throwsResourceNotFoundException() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookService.findBooksByAuthorId(99L, PageRequest.of(0, 10)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Author not found with id: 99");
    }
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