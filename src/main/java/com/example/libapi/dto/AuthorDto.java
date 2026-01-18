package com.example.libapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class AuthorDto {
    private Long id;
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