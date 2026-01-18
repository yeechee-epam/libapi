package com.example.libapi.controller;

import com.example.libapi.dto.AuthorDto;
import com.example.libapi.dto.AuthorWithBooksPageDto;
import com.example.libapi.dto.BookDto;
import com.example.libapi.mapper.AuthorMapper;
import com.example.libapi.service.AuthorService;
import com.example.libapi.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authors")
@Tag(name = "Authors", description = "Operations related to authors")
public class AuthorController {
    private final AuthorService authorService;
    private final AuthorMapper authorMapper;
    private final BookService bookService;

    @Autowired
    public AuthorController(AuthorService authorService, AuthorMapper authorMapper,BookService bookService) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
        this.bookService = bookService;
    }

    @Operation(
            summary = "Get paginated list of authors",
            description = "Returns a paginated list of all available authors."
    )
    @GetMapping
    public ResponseEntity<Page<AuthorDto>> listAuthors(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        Page<AuthorDto> result = authorService.list(PageRequest.of(page, size))
                .map(authorMapper::toDto);
        return ResponseEntity.ok(result);
    }
//    @Operation(
//            summary = "Get details of a specific author",
//            description = "Returns the details of an author and all their books."
//    )
//    @GetMapping("/{id}")
//    public ResponseEntity<AuthorDto> getAuthorById(
//            @Parameter(description = "Author ID") @PathVariable Long id
//    ) {
//        return authorService.getAuthorById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
@Operation(
        summary = "Get details of a specific author",
        description = "Returns the details of an author and a paginated list of their books."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Author found",
                content = @Content(schema = @Schema(implementation = AuthorWithBooksPageDto.class))),
        @ApiResponse(responseCode = "404", description = "Author not found")
})
@GetMapping("/{id}")
public ResponseEntity<AuthorWithBooksPageDto> getAuthorById(
        @Parameter(description = "Author ID") @PathVariable Long id,
        @ParameterObject Pageable pageable
) {
//    return authorService.getAuthorWithBooksPage(id, pageable)
//            .map(ResponseEntity::ok)
//            .orElse(ResponseEntity.notFound().build());
    AuthorWithBooksPageDto dto = authorService.getAuthorWithBooksPage(id, pageable);
    return ResponseEntity.ok(dto);
}

    @Operation(
            summary = "Get paginated list of books by author",
            description = "Returns a paginated list of all books for a specific author."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Books found"),
            @ApiResponse(responseCode = "404", description = "Author not found"),
            @ApiResponse(responseCode = "400", description = "Invalid author ID format")
    })
    @GetMapping("/{id}/books")
    public ResponseEntity<Page<BookDto>> getBooksByAuthor(
            @Parameter(description = "Author ID") @PathVariable Long id,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        Page<BookDto> result = bookService.findBooksByAuthorId(id, PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Create a new author",
            description = "Creates a new author and returns the created author details."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Author created",
                    content = @Content(schema = @Schema(implementation = AuthorDto.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate author"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<AuthorDto> createAuthor(@Validated @RequestBody AuthorDto authorDto) {
        AuthorDto created = authorService.createAuthor(authorDto);
        return ResponseEntity.status(201).body(created);
    }

}