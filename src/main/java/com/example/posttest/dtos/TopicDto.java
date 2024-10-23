package com.example.posttest.dtos;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TopicDto {

    private String topic_title;

    private String subject_title;

    private Long member_id;


    private String writer_email;


    private Long deadline;

    private Long topic_id;


    private String introduction_text;

    public TopicDto(String topic_title,String subject_title,Long deadline,String introduction_text) {
        this.topic_title = topic_title;
        this.subject_title=subject_title;
        this.deadline=deadline;
        this.introduction_text=introduction_text;
    }


    public TopicDto(String topic_title, Long member_id) {
        this.topic_title = topic_title;
        this.member_id = member_id;
    }

    public TopicDto(String topic_title, Long member_id, Long topic_id,String subject_title,String writer_email) {
        this.topic_title = topic_title;
        this.member_id = member_id;
        this.topic_id = topic_id;
        this.subject_title=subject_title;
        this.writer_email=writer_email;

    }


}
