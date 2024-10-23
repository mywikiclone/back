package com.example.posttest.entitiy;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class DiscussionComment extends Times{


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long comment_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="topic_id")
    private DiscussionTopic discussionTopic;


    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;



    private String comment_content;


    public DiscussionComment(DiscussionTopic discussionTopic,Member member, String comment_content) {

        this.discussionTopic=discussionTopic;
        this.member = member;
        this.comment_content = comment_content;

    }




}
