package com.example.posttest.entitiy;

import com.example.posttest.etc.UserAdmin;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@DynamicInsert//defaultvalue가 자꾸null로 들어가길래 추가한것.
public class Member extends Times{


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long member_id;



    @Version
    private Long version;


    private String email;

    @Column
    private String password;




    @Enumerated(EnumType.STRING)
    private UserAdmin grade;


    private String access_ip;

    @ColumnDefault(" 'false' ")
    private String oauth2_login;



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



    public Member(String email,UserAdmin grade,String oauth2_login) {
        this.email = email;
        this.grade = grade;
        this.oauth2_login=oauth2_login;

    }
}
