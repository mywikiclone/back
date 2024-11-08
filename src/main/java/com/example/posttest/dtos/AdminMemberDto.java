package com.example.posttest.dtos;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AdminMemberDto {

    private String email;


    private String grade;

    private LocalDateTime create_time;
    public AdminMemberDto(String email, String grade) {
        this.email = email;
        this.grade = grade;
    }

    public AdminMemberDto(String email, String grade, LocalDateTime create_time) {
        this.email = email;
        this.grade = grade;
        this.create_time = create_time;
    }
}
