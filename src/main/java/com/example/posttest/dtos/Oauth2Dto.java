package com.example.posttest.dtos;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Oauth2Dto {


    private String email;


    public Oauth2Dto(String email) {

        this.email = email;
    }
}
