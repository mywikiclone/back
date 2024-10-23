package com.example.posttest.dtos;

import com.example.posttest.entitiy.Member;
import com.example.posttest.entitiy.Times;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class DiscussionCommentDtos  {


    private Long topic_id;

    private Long member_id;


    private String comment_content;

    private String writer_email;



    private LocalDateTime Create_Time;


    public DiscussionCommentDtos(Long topic_id,String comment_content) {
        this.topic_id=topic_id;
        this.comment_content = comment_content;

    }


    public DiscussionCommentDtos(Long topic_id,String writer_email,Long member_id, String comment_content, LocalDateTime create_Time) {
        this.topic_id = topic_id;
        this.writer_email=writer_email;
        this.member_id = member_id;
        this.comment_content = comment_content;
        this.Create_Time = create_Time;
    }
}
