package com.example.libapi.controller;

import com.example.libapi.dto.BookDto;
import com.example.libapi.entity.Book;
import com.example.libapi.repository.BookRepository;
import com.example.libapi.service.BookService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
//    private final BookRepository bookRepository;

    public BookController(BookService bookService, BookRepository bookRepository) {
        this.bookService = bookService;
//        this.bookRepository = bookRepository;
    }

    @GetMapping
    public Page<Book> listBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return bookService.list(pageable);
    }

}
