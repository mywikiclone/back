package com.example.posttest.repository.changelogrepo;

import com.example.posttest.dtos.ChangeLogDto;
import com.example.posttest.dtos.ChangeLogListDto;
import com.example.posttest.entitiy.ChangeLog;
import com.example.posttest.entitiy.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChangeLongRepo extends JpaRepository<ChangeLog,Long> {

    @Query("select new com.example.posttest.dtos.ChangeLogDto(c.Log_Id,c.Create_Time,c.member.member_id,c.member.email) from ChangeLog c where c.content.content_id=:id order by c.Create_Time asc ")
    public Page<ChangeLogDto> getchangelogs(@Param("id") Long id, Pageable pageable);


    @Query("select new com.example.posttest.dtos.ChangeLogListDto(c.Log_Id,c.content.title,c.member.email,c.Create_Time,c.content.content_id) from ChangeLog c order by c.Create_Time asc ")
    public Page<ChangeLogListDto> getchangelogs(Pageable pageable);




    @Query("select distinct c.content from ChangeLog c where c.Create_Time>:now")
    public List<Content> getatleastchages(@Param("now")LocalDateTime now);

}
