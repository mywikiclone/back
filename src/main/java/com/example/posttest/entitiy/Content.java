package com.example.posttest.entitiy;

import com.example.posttest.etc.UserAdmin;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;

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

/*@Lob
    private String content;*/


    @OneToOne(fetch =FetchType.LAZY)
    @JoinColumn(name="lob_id")
    private LobContent lobContent;


    @OneToOne(fetch =FetchType.LAZY)

    private ContentAdmin grade;


    public Content(String title, LobContent content) {
        this.title = title;
        this.lobContent= content;
    }


    public Content(Member member, String title, LobContent content) {
        this.member = member;
        this.title = title;
        this.lobContent = content;
    }


    public Content( Member member, String title, LobContent content, ContentAdmin grade) {
        this.member = member;
        this.title = title;
        this.lobContent = content;
        this.grade = grade;
    }

    public static Comparator<Content> byUpdateTime() {
        return Comparator.comparing(Content::getUpdate_Time);
    }

}
