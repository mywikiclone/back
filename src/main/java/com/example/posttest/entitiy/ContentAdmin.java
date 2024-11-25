package com.example.posttest.entitiy;

import com.example.posttest.etc.UserAdmin;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ContentAdmin {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long admin_id;



    @Enumerated(EnumType.STRING)
    private UserAdmin grade;

    public ContentAdmin(UserAdmin grade) {
        this.grade = grade;
    }
}
