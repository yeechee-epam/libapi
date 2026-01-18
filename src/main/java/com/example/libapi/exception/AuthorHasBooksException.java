package com.example.libapi.exception;

public class AuthorHasBooksException extends RuntimeException {
    public AuthorHasBooksException(String message) {
        super(message);
    }
}