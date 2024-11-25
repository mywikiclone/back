package com.example.posttest.entitiy;

import com.example.posttest.etc.UserAdmin;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.web.WebProperties;

@Entity
@Data
@NoArgsConstructor
public class Member extends Times{


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long member_id;



    @Version
    private Long version;


    private String email;

    @Column
    private String password;




    @OneToOne(fetch = FetchType.LAZY)
    private UserAdmins grade;


    public Member(String email, String password,UserAdmins grade) {
        this.email = email;
        this.password = password;
        this.grade = grade;
    }

    public Member(Long member_id, String email, String password,UserAdmins grade) {
        this.member_id = member_id;
        this.email = email;
        this.password = password;
        this.grade = grade;
    }
}
