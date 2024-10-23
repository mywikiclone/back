package com.example.posttest.entitiy;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Data
public class DiscussionTopic extends Times{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long topic_id;



    @Column(name="origin_proposer_id")
    private long origin_proposer_id;


    private String writer_email;

    private String subject_title;

    private String topic_title;

    private LocalDateTime deadline;


    private String introduction_text;

    public DiscussionTopic(long origin_proposer_id,String writer_email, String topic_title,String subject_title,LocalDateTime deadline,String introduction_text) {
        this.origin_proposer_id = origin_proposer_id;
        this.writer_email=writer_email;
        this.topic_title = topic_title;
        this.subject_title=subject_title;
        this.deadline=deadline;
        this.introduction_text=introduction_text;

    }
}
