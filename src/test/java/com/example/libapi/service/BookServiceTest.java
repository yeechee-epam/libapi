package com.example.libapi.service;

import com.example.libapi.dto.BookDto;
import com.example.libapi.entity.Author;
import com.example.libapi.entity.Book;
import com.example.libapi.mapper.BookMapper;
import com.example.libapi.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Test
    void testListBooks() {
        BookRepository bookRepository = mock(BookRepository.class);
        BookMapper bookMapper = mock(BookMapper.class);

        BookService bookService = new BookService(bookRepository, bookMapper);

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