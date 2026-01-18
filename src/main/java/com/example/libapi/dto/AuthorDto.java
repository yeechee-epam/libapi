package com.example.libapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class AuthorDto {
    private Long id;
    @NotBlank(message = "Author name is required")
    @Size(max = 50, message = "Author name must be <= 50 characters")
    private String name;
    //    kan-45 view author page
    private List<BookSummaryDto> books; // Add this

    @Data
    public static class BookSummaryDto {
        private Long id;

        private String name;
        private String bookLink;//to direct to book page

    }
//    end of kan-45
}