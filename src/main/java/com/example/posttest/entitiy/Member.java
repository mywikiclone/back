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




    @Column
    private String email;

    @Column
    private String password;



    @Enumerated(EnumType.STRING)
    private UserAdmin grade;


    public Member(String email, String password,UserAdmin grade) {
        this.email = email;
        this.password = password;
        this.grade = grade;
    }

    public Member(Long member_id, String email, String password,UserAdmin grade) {
        this.member_id = member_id;
        this.email = email;
        this.password = password;
        this.grade = grade;
    }
}
