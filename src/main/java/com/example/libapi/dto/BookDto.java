package com.example.libapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookDto {
    private Long id;
    @NotBlank(message = "book name is required")
    private String name;
    private Long authorId;//existing author
    private String authorName;//new author or search
    private String authorLink;
}
