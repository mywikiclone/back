package com.example.posttest.entitiy;


import com.example.posttest.entitiy.Content;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class RealTimeIssue {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long issue_id;


    @ManyToOne
    @JoinColumn(name="content_id")
    private Content content;




    private LocalDateTime searched_time;

}
