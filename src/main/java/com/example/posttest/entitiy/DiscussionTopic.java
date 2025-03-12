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



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="proposer_id")
    private Member member;


    private String topic_title;



    @ManyToOne
    @JoinColumn(name="content_id")
    private Content content;


    private LocalDateTime deadline;


    private String introduction_text;

    public DiscussionTopic(Member member,String topic_title,Content content,LocalDateTime deadline,String introduction_text) {
        this.member=member;
        this.topic_title = topic_title;
        this.content=content;
        this.deadline=deadline;
        this.introduction_text=introduction_text;

    }
}
