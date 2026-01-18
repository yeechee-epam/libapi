package com.example.libapi.mapper;

import com.example.libapi.dto.BookDto;
import com.example.libapi.entity.Author;
import com.example.libapi.entity.Book;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.name", target = "authorName")
    BookDto toDto(Book book);

    @Mapping(target = "author", expression = "java(authorFromDto(bookDto))")
    Book toEntity(BookDto bookDto);

    // Helper method for mapping from DTO to Author entity
    default Author authorFromDto(BookDto bookDto) {
        if (bookDto.getAuthorId() != null) {
            Author author = new Author();
            author.setId(bookDto.getAuthorId());
            if (bookDto.getAuthorName() != null && !bookDto.getAuthorName().isBlank()) {
                author.setName(bookDto.getAuthorName().trim());
            }
            return author;
        } else if (bookDto.getAuthorName() != null && !bookDto.getAuthorName().isBlank()) {
            Author author = new Author();
            author.setName(bookDto.getAuthorName().trim());
            return author;
        }
        return null;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "author", expression = "java(authorFromDto(bookDto))")
    void updateBookFromDto(BookDto bookDto, @MappingTarget Book book);
}