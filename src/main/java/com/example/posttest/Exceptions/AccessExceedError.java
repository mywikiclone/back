package com.example.posttest.Exceptions;


import lombok.Data;



@Data
public class AccessExceedError extends RuntimeException {




    private String email;


    public AccessExceedError(String message,String email) {
        super(message);
        this.email = email;
    }
}
