package com.example.libapi.mapper;

import com.example.libapi.dto.AuthorDto;
import com.example.libapi.entity.Author;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class AuthorMapperTest {

    private final AuthorMapper authorMapper = Mappers.getMapper(AuthorMapper.class);

    @Test
    void testToDto() {
        Author author = Author.builder().id(1L).name("Author Name").build();
        AuthorDto dto = authorMapper.toDto(author);

        assertEquals(author.getId(), dto.getId());
        assertEquals(author.getName(), dto.getName());
    }

    @Test
    void testToEntity() {
        AuthorDto dto = new AuthorDto();
        dto.setId(1L);
        dto.setName("Author Name");

        Author author = authorMapper.toEntity(dto);

        assertEquals(dto.getId(), author.getId());
        assertEquals(dto.getName(), author.getName());
    }
}