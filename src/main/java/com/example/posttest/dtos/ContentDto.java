package com.example.posttest.dtos;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContentDto {

    private Long content_id;

    private String title;

    private String content;




    public ContentDto(Long content_id, String title, String content) {
        this.content_id = content_id;
        this.title = title;
        this.content = content;
    }
}
