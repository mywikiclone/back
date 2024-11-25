package com.example.posttest.dtos;


import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserSession {


    private Long member_id;
    private String crsf;


    public UserSession(Long member_id, String crsf) {
        this.member_id = member_id;
        this.crsf = crsf;
    }
}
