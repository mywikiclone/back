package com.example.posttest.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MailAuthDto {


    private String mail_code;


    public MailAuthDto(String mail_code) {
        this.mail_code = mail_code;
    }
}
