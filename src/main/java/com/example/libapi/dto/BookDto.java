package com.example.libapi.dto;

import lombok.Data;

@Data
public class BookDto {
    private Long id;
    private String name;
    private Long authorId;//existing author
    private String authorName;//new author or search
    private String authorLink;
}
