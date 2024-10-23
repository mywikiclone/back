package com.example.posttest.entitiy;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;

@Entity
@Data
@NoArgsConstructor
public class Content extends Times {



    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long content_id;


    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;


    private String title;

    @Lob
    private String content;


    public Content(String title, String content) {
        this.title = title;
        this.content = content;
    }


    public Content(Member member, String title, String content) {
        this.member = member;
        this.title = title;
        this.content = content;
    }

    public static Comparator<Content> byUpdateTime() {
        return Comparator.comparing(Content::getUpdate_Time);
    }

}
