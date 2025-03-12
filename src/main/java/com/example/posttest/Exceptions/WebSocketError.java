package com.example.posttest.Exceptions;

public class WebSocketError extends RuntimeException{


    public WebSocketError(String message) {
        super(message);
    }
}
