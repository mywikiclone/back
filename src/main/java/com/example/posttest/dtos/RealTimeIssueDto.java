package com.example.posttest.dtos;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RealTimeIssueDto {


    private String title;
    private Long content_id;


    public RealTimeIssueDto(String title, Long content_id) {
        this.title = title;
        this.content_id = content_id;
    }
}
