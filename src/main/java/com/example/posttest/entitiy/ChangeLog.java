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




    @OneToOne(fetch = FetchType.LAZY)
    private LobContent lobContent;



    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;


    public ChangeLog(Content content, LobContent lobContent, Member member) {
        this.content=content;
        this.lobContent = lobContent;
        this.member = member;
    }
}
