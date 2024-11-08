package com.example.posttest.dtos;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {


    private String email;

    private String password;

    public MemberDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
