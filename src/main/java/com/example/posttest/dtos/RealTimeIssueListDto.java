package com.example.posttest.dtos;


import com.example.posttest.entitiy.Content;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;

@Data
@NoArgsConstructor
public class RealTimeIssueListDto {


    private Long content_id;

    private String title;

    private Long count;

    public RealTimeIssueListDto(Long content_id, String title, Long count) {
        this.content_id = content_id;
        this.title = title;
        this.count = count;
    }

    public static Comparator<RealTimeIssueListDto> CompareByCount() {
        return Comparator.comparing(RealTimeIssueListDto::getCount);
    }
}
