package com.example.posttest.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserMessage {


    private String sender;
    private String msg;

    public UserMessage(String sender, String msg) {
        this.sender = sender;
        this.msg = msg;
    }
}
