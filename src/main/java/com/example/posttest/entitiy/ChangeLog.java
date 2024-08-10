package com.example.posttest.entitiy;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ChangeLog extends Times {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long Log_Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="content_id")
    private Content content;


    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;




    private String Changed_Content;

    public ChangeLog(Content content, String changed_Content,Member member) {
        this.content = content;
        this.Changed_Content = changed_Content;
        this.member=member;
    }
}
