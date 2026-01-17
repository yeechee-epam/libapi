package com.example.libapi.service;

import com.example.libapi.dto.BookDto;
import com.example.libapi.entity.Author;
import com.example.libapi.entity.Book;
import com.example.libapi.repository.AuthorRepository;
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
public BookService(BookRepository bookRepository,AuthorRepository authorRepository,AuthorService authorService)
    {
        this.bookRepository=bookRepository;
    }

    public Page<Book> list(Pageable pageable)
    {
        return bookRepository.findAll(pageable);
    }


}
