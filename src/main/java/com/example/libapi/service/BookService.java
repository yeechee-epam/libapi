package com.example.libapi.service;

import com.example.libapi.dto.BookDto;
import com.example.libapi.entity.Author;
import com.example.libapi.entity.Book;
//import com.example.libapi.repository.AuthorRepository;
import com.example.libapi.mapper.BookMapper;
import com.example.libapi.repository.BookRepository;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

public BookService(BookRepository bookRepository,BookMapper bookMapper)
    {
        this.bookRepository=bookRepository;
        this.bookMapper=bookMapper;
    }

    public Page<BookDto> list(Pageable pageable)
    {
        return bookRepository
                .findAll(pageable)
                .map(bookMapper::toDto);
    }

//    public Optional<BookDto> getBookById(Long id) {
//        return bookRepository.findById(id).map(bookMapper::toDto);
//    }
//    map dto to include author link
public Optional<BookDto> getBookById(Long id) {
    return bookRepository.findById(id).map(book -> {
        BookDto dto = bookMapper.toDto(book);
        if (book.getAuthor() != null && book.getAuthor().getId() != null) {
            dto.setAuthorLink("/authors/" + book.getAuthor().getId());
        }
        return dto;
    });
}



}
