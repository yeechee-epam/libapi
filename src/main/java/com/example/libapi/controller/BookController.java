package com.example.libapi.controller;


import com.example.libapi.dto.BookDto;
import com.example.libapi.entity.Book;
import com.example.libapi.exception.DuplicateBookException;
import com.example.libapi.exception.ResourceNotFoundException;
import com.example.libapi.mapper.BookMapper;
import com.example.libapi.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
    private final BookMapper bookMapper;

    public BookController(BookService bookService,BookMapper bookMapper) {

        this.bookService = bookService;
        this.bookMapper = bookMapper;
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

//    create new book
@Operation(
        summary = "Create a new book",
        description = "Creates a new book and returns the created book details including author info."
)
@ApiResponses({
        @ApiResponse(responseCode = "201", description = "Book created"),
        @ApiResponse(responseCode = "409", description = "Duplicate book (same name and author)"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
})
@PostMapping
public ResponseEntity<BookDto> createBook(@Validated @RequestBody BookDto bookDto) {
    try {
        Book created = bookService.create(bookDto);
        BookDto result = bookMapper.toDto(created);
        result.setAuthorLink("/authors/" + created.getAuthor().getId());
        return ResponseEntity.status(201).body(result);
    } catch (DuplicateBookException e) {
        return ResponseEntity.status(409).build();
    }
}

//PUT book
@Operation(
        summary = "Update a book's details",
        description = "Updates the details of a book. Returns 404 if the book does not exist."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Book updated",
                content = @Content(schema = @Schema(implementation = BookDto.class))),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
})
@PutMapping("/{id}")
public ResponseEntity<BookDto> updateBook(
        @Parameter(description = "Book ID") @PathVariable Long id,
        @Validated @RequestBody BookDto bookDto
) {
    Book updated = bookService.updateBook(id, bookDto);
    BookDto result = bookMapper.toDto(updated);
    result.setAuthorLink("/authors/" + updated.getAuthor().getId());
    return ResponseEntity.ok(result);
}

//delete book
@Operation(
        summary = "Delete a book",
        description = "Deletes a book by its ID. Returns 204 if successful, 404 if not found."
)
@ApiResponses({
        @ApiResponse(responseCode = "204", description = "Book deleted"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "400", description = "Invalid book ID format")
})
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteBook(
        @Parameter(description = "Book ID") @PathVariable Long id
) {
    bookService.deleteBook(id);
    return ResponseEntity.noContent().build();
}

}