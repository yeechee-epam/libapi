package com.example.libapi.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class AuthorWithBooksPageDto {
    private Long id;
    private String name;
    private Page<BookSummaryDto> books; // paginated books

    @Data
    public static class BookSummaryDto {
        private Long id;
        private String name;
        private String bookLink;
    }
}