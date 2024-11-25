package com.example.posttest.entitiy;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class LobContent {


    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long lob_id;


    @Lob
    private String content;


    public LobContent(String content) {
        this.content = content;
    }
}