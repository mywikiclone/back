package com.example.posttest.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MailAuthDto2 {

    private String email;
    private String code;

    public MailAuthDto2(String email, String code) {
        this.email = email;
        this.code = code;
    }
}
