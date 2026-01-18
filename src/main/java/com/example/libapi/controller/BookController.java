package com.example.libapi.controller;

import com.example.libapi.dto.BookDto;
import com.example.libapi.exception.ResourceNotFoundException;
import com.example.libapi.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@Tag(name = "Books", description = "Operations related to books")
@Validated
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
    public ResponseEntity<Page<BookDto>> listBooks(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        Page<BookDto> result = bookService.list(PageRequest.of(page, size));
        return ResponseEntity.ok(result); // Explicitly returns 200 OK
    }
    @Operation(
            summary = "Get details of a specific book",
            description = "Returns the details of a book, including its author."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "400", description = "Invalid book ID format")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(
            @Parameter(description = "Book ID") @PathVariable Long id
    ) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
                .orElseThrow(()->new ResourceNotFoundException("Book not found with id: "+id));
    }
}