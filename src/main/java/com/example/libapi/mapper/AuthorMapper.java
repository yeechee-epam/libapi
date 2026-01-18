package com.example.libapi.mapper;

import com.example.libapi.dto.AuthorDto;
import com.example.libapi.entity.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    AuthorDto toDto(Author author);
    Author toEntity(AuthorDto authorDto);
}