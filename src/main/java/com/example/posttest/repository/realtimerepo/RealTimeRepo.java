package com.example.posttest.repository.realtimerepo;

import com.example.posttest.dtos.RealTimeIssueDto;
import com.example.posttest.dtos.RealTimeIssueListDto;
import com.example.posttest.entitiy.RealTimeIssue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;

public interface RealTimeRepo extends JpaRepository<RealTimeIssue,Long> {



    @Query("select new com.example.posttest.dtos.RealTimeIssueListDto(i.content.content_id,i.content.title,count(i)) from RealTimeIssue i " +
            "where i.searched_time>:now " +
            "group by i.content.content_id,i.content.title order by count(i) desc")
    public Page<RealTimeIssueListDto> getrealtime(Pageable pageable, @Param("now") LocalDateTime now);
}
