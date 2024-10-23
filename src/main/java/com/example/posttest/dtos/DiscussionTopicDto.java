package com.example.posttest.dtos;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class DiscussionTopicDto {


    private String writer_email;

    private String subject_title;

    private String topic_title;

    private LocalDateTime deadline;


    private String introduction_text;


    private long topic_id;

    public DiscussionTopicDto(String writer_email, String subject_title, String topic_title, LocalDateTime deadline, String introduction_text) {
        this.writer_email = writer_email;
        this.subject_title = subject_title;
        this.topic_title = topic_title;
        this.deadline = deadline;
        this.introduction_text = introduction_text;
    }


    public DiscussionTopicDto(String writer_email, String subject_title, String topic_title, LocalDateTime deadline, String introduction_text, long topic_id) {
        this.writer_email = writer_email;
        this.subject_title = subject_title;
        this.topic_title = topic_title;
        this.deadline = deadline;
        this.introduction_text = introduction_text;
        this.topic_id = topic_id;
    }
}
