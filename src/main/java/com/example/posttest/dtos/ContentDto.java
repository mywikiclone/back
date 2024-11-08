package com.example.posttest.dtos;


import com.example.posttest.etc.UserAdmin;
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

    private LocalDateTime create_time;

    private String grade;


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


    public ContentDto(Long content_id, String title, String content, String email, LocalDateTime update_time,String grade) {
        this.content_id = content_id;
        this.title = title;
        this.content = content;
        this.email = email;
        this.update_time = update_time;
        this.grade = grade;
    }


    public ContentDto(Long content_id, String title,String email, LocalDateTime update_time, LocalDateTime create_time, String grade) {
        this.content_id = content_id;
        this.title = title;
        this.email = email;
        this.update_time = update_time;
        this.create_time = create_time;
        this.grade = grade;
    }
}
