package com.example.libapi.controller;

import com.example.libapi.dto.BookDto;
import com.example.libapi.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@Tag(name = "Books", description = "Operations related to books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(
            summary = "Get paginated list of books",
            description = "Returns a paginated list of all available books."
    )
    @GetMapping
    public Page<BookDto> listBooks(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size
    ) {
        return bookService.list(PageRequest.of(page, size));
    }
}