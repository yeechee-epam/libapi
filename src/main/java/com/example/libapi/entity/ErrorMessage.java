package com.example.libapi.entity;

//package com.example.hello.models;

//package com.example.helloworld.models;

import lombok.Value;

@Value
public class ErrorMessage {

    private String message;

    public static ErrorMessage from(final String message) {
        return new ErrorMessage(message);
    }
}

