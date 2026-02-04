package com.example.libapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookDto {
    private Long id;
    @NotBlank(message = "book name is required")
    @Size(max = 50,message = "book name must be <= 50 characters")
    private String name;
//    authorId or authorName
    private Long authorId;//existing author
    @NotBlank(message = "author name is required")
    @Size(max = 50,message = "author name must be <= 50 characters")
    private String authorName;//new author or search
    private String authorLink;
//for admin (logged in) only
    private Boolean recommendedByMe;

}
