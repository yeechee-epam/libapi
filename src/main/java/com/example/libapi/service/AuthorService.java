package com.example.libapi.service;

import com.example.libapi.dto.AuthorDto;
import com.example.libapi.dto.AuthorWithBooksPageDto;
import com.example.libapi.entity.Author;
import com.example.libapi.entity.Book;
import com.example.libapi.exception.ResourceNotFoundException;
import com.example.libapi.mapper.AuthorMapper;
import com.example.libapi.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Autowired
    public AuthorService(AuthorRepository authorRepository,AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    public Page<Author> list(Pageable pageable) {
        return authorRepository.findAll(pageable);
    }
    public AuthorDto getAuthorById(Long id) {
        return authorRepository.findById(id).map(authorMapper::toDto)
                .orElseThrow(()->new ResourceNotFoundException("Author not found with id: "+id));
    }
    public AuthorWithBooksPageDto getAuthorWithBooksPage(Long id, Pageable pageable) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        List<Book> books = author.getBooks();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), books.size());
        List<AuthorWithBooksPageDto.BookSummaryDto> bookDtos = books.subList(start, end).stream()
                .map(book -> {
                    AuthorWithBooksPageDto.BookSummaryDto dto = new AuthorWithBooksPageDto.BookSummaryDto();
                    dto.setId(book.getId());
                    dto.setName(book.getName());
                    dto.setBookLink("/books/" + book.getId());
                    return dto;
                }).collect(Collectors.toList());
        Page<AuthorWithBooksPageDto.BookSummaryDto> bookPage = new PageImpl<>(bookDtos, pageable, books.size());

        AuthorWithBooksPageDto dto = new AuthorWithBooksPageDto();
        dto.setId(author.getId());
        dto.setName(author.getName());
        dto.setBooks(bookPage);
        return dto;
    }
}