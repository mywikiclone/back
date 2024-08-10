package com.example.posttest.entitiy;


import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@MappedSuperclass
@Getter
@Setter
public class Times {

    @Column(nullable = true)
    private LocalDateTime Create_Time;


    @Column(name="update_time")
    private LocalDateTime Update_Time;

    @Column(nullable = true)
    private LocalDateTime Delete_Time;

}
