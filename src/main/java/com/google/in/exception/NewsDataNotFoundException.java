package com.google.in.exception;


public class NewsDataNotFoundException extends RuntimeException {

    public NewsDataNotFoundException(String message) {
        super(message);
    }

    public NewsDataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

