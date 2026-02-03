package com.example.libapi.controller;

import com.example.libapi.dto.BookDto;
import com.example.libapi.mapper.BookMapper;
import com.example.libapi.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/recommendations")
@PreAuthorize("hasRole('admin')")
@Tag(name = "Admin Recommendations", description = "Admin-only recommendation operations")
public class AdminRecommendationController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    public AdminRecommendationController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    @Operation(
            summary = "Get books recommended by current admin",
            description = "Returns all books recommended by the currently authenticated admin."
    )
    @GetMapping("/books")
    public ResponseEntity<List<BookDto>> getMyRecommendedBooks() {
        List<BookDto> result = bookService.getBooksRecommendedByCurrentAdmin()
                .stream()
                .map(bookMapper::toDto)
                .toList();

        return ResponseEntity.ok(result);
    }
}

