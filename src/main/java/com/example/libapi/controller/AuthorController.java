package com.example.libapi.controller;

import com.example.libapi.dto.AuthorDto;
import com.example.libapi.mapper.AuthorMapper;
import com.example.libapi.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authors")
@Tag(name = "Authors", description = "Operations related to authors")
public class AuthorController {
    private final AuthorService authorService;
    private final AuthorMapper authorMapper;

    @Autowired
    public AuthorController(AuthorService authorService, AuthorMapper authorMapper) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
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
    @Operation(
            summary = "Get details of a specific author",
            description = "Returns the details of an author and all their books."
    )
    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getAuthorById(
            @Parameter(description = "Author ID") @PathVariable Long id
    ) {
        return authorService.getAuthorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}