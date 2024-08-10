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


    private String title;


    private String content;


    public Content(String title, String content) {
        this.title = title;
        this.content = content;
    }
    public static Comparator<Content> byUpdateTime() {
        return Comparator.comparing(Content::getUpdate_Time);
    }

}
