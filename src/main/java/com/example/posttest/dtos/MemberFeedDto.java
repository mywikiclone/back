package com.example.posttest.dtos;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class MemberFeedDto {

    private Long member_id;

    private Long topic_id;

    private String feed_check;

    public MemberFeedDto(Long member_id, Long topic_id, String sign) {
        this.member_id = member_id;
        this.topic_id = topic_id;
        this.feed_check = sign;
    }
}
