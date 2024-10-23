package com.example.posttest.entitiy;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.web.WebProperties;

@Entity
@Data
@NoArgsConstructor
public class Member {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long member_id;


    @Column
    private String salt;

    @Column
    private String email;

    @Column
    private String password;

    public Member(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Member(String salt, String email, String password) {

        this.salt = salt;
        this.email = email;
        this.password = password;
    }
}
