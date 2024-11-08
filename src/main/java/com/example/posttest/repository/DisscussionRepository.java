package com.example.posttest.repository;

import com.example.posttest.dtos.ChangeLogDto;
import com.example.posttest.dtos.TopicDto;
import com.example.posttest.entitiy.ChangeLog;
import com.example.posttest.entitiy.DiscussionComment;
import com.example.posttest.entitiy.DiscussionTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DisscussionRepository extends JpaRepository<DiscussionTopic,Long> {



    @Query("select new com.example.posttest.dtos.TopicDto(d.topic_title,d.origin_proposer_id,d.topic_id,d.subject_title,d.writer_email,d.deadline) from DiscussionTopic d order by d.Create_Time asc ")
    public Page<TopicDto> gettopics(Pageable pageable);
}
