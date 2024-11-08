package com.example.posttest.dtos;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdminContentDto {





    private Long content_id;

    private  String grade;

    public AdminContentDto(Long content_id, String grade) {
        this.content_id = content_id;
        this.grade = grade;
    }
}
