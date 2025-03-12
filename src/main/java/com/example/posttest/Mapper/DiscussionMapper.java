package com.example.posttest.Mapper;


import com.example.posttest.dtos.ContentDto;
import com.example.posttest.dtos.RealTimeIssueDto;
import com.example.posttest.entitiy.Content;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DiscussionMapper {



    List<Long> getdiscussionid(Long mebmer_id);


    List<ContentDto> get_content_list();


    List<RealTimeIssueDto>  get_change_logs(LocalDateTime now);



    List<Long>  get_feed_list(@Param("id") Long id,@Param("member_id") Long member_id);
}
