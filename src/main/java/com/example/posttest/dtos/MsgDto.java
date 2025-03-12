package com.example.posttest.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MsgDto {
    private String main_topic;
    private String room_id;
    private Long discussion_id;
    private String msg;
}
