package com.example.libapi.mapper;

import com.example.libapi.dto.BookDto;
import com.example.libapi.entity.Author;
import com.example.libapi.entity.Book;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class BookMapperTest {

    private final BookMapper bookMapper = Mappers.getMapper(BookMapper.class);

    @Test
    void testToDto() {
        Author author = Author.builder().id(1L).name("Author Name").build();
        Book book = Book.builder().id(10L).name("Book Title").author(author).build();

        BookDto dto = bookMapper.toDto(book);

        assertEquals(book.getName(), dto.getName());
        assertEquals(author.getId(), dto.getAuthorId());
        assertEquals(author.getName(), dto.getAuthorName());
    }

    @Test
    void testToEntity() {
        BookDto dto = new BookDto();
        dto.setName("Book Title");
        dto.setAuthorId(1L);
        dto.setAuthorName("Author Name");

        Book book = bookMapper.toEntity(dto);

        assertEquals(dto.getName(), book.getName());
        assertNotNull(book.getAuthor());
        assertEquals(dto.getAuthorId(), book.getAuthor().getId());
        assertEquals(dto.getAuthorName(), book.getAuthor().getName());
    }
}