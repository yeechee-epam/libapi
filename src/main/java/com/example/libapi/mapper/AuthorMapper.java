package com.example.libapi.mapper;

import com.example.libapi.dto.AuthorDto;
import com.example.libapi.entity.Author;
import com.example.libapi.entity.Book;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    @Mapping(target = "books", expression = "java(mapBooks(author.getBooks()))")
    AuthorDto toDto(Author author);

    Author toEntity(AuthorDto authorDto);

    default List<AuthorDto.BookSummaryDto> mapBooks(List<Book> books) {
        if (books == null) return null;
        return books.stream().map(book -> {
            AuthorDto.BookSummaryDto dto = new AuthorDto.BookSummaryDto();
            dto.setId(book.getId());
            dto.setName(book.getName());
            dto.setBookLink("/books/" + book.getId());
            return dto;
        }).collect(Collectors.toList());
    }
}