package com.example.posttest.dtos;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ContentDto {

    private Long content_id;

    private String title;

    private String content;

    private String email;

    private LocalDateTime update_time;

    public ContentDto(Long content_id, String title, String content,LocalDateTime update_time) {
        this.content_id = content_id;
        this.title = title;
        this.content = content;
        this.update_time=update_time;
    }


    public ContentDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public ContentDto(Long content_id, String title, String content, String email, LocalDateTime update_time) {
        this.content_id = content_id;
        this.title = title;
        this.content = content;
        this.email = email;
        this.update_time = update_time;
    }
}
