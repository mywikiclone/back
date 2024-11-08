package com.example.posttest.dtos;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChangeLogListDto {

    private long log_id;

    private Long content_id;


    private String content_title;


    private String user_email;

    private LocalDateTime create_time;

    public ChangeLogListDto(long log_id, String content_title, String user_email, LocalDateTime create_time,Long content_id) {
        this.log_id = log_id;
        this.content_title =content_title;
        this.user_email = user_email;
        this.create_time=create_time;
        this.content_id=content_id;
    }
}
