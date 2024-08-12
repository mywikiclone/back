package com.example.posttest.dtos;

import java.time.LocalDateTime;



import com.example.posttest.entitiy.Member;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChangeLogDto {
    private Long log_id;
    private LocalDateTime update_time;
    private Long member_id;
    private String member_name;

    public ChangeLogDto(Long log_id, LocalDateTime update_time,Long member_id,String member_name) {
        this.log_id = log_id;
        this.update_time = update_time;
        this.member_id=member_id;
        this.member_name=member_name;
    }
}
