package com.example.posttest.repository;
import com.example.posttest.dtos.DiscussionCommentDtos;
import com.example.posttest.entitiy.DiscussionComment;
import com.example.posttest.entitiy.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DiscussionCommentRepo extends JpaRepository<DiscussionComment,Long> {




    @Query("select distinct new com.example.posttest.dtos.DiscussionCommentDtos(d.discussionTopic.topic_id,d.member.email,d.member.member_id,d.comment_content,d.Create_Time) " +
            "from DiscussionComment d " +
            "where d.discussionTopic.topic_id = :topic_id " +
            "order by d.Create_Time asc")
    //애같은경우 orderby에 쓰인개 select likst에 들어가ㅑ된다 일바적으로 그게맞다 단 pageable를 같이쓴경우에는 없ㅎ어도무관.
    //그냥 웬만하면 넣어주도록하자 일관성잇게.
    public Page<DiscussionCommentDtos> get_comment_list(@Param("topic_id") Long topic_id , Pageable page);


    @Query("select distinct new com.example.posttest.dtos.DiscussionCommentDtos(d.discussionTopic.topic_id,d.member.email,d.member.member_id,d.comment_content,d.Create_Time) " +
            "from DiscussionComment d " +
            "where d.discussionTopic.topic_id = :topic_id " +
            "order by d.Create_Time asc")
    //애같은경우 orderby에 쓰인개 select likst에 들어가ㅑ된다 일바적으로 그게맞다 단 pageable를 같이쓴경우에는 없ㅎ어도무관.
    //그냥 웬만하면 넣어주도록하자 일관성잇게.
    public List<DiscussionCommentDtos> get_comment_list2(@Param("topic_id") Long topic_id);


    /*@Query("select distinct new com.example.posttest.dtos.DiscussionCommentDtos(d.discussionTopic.topic_id,d.member.member_id,d.comment_content,d.Create_Time) " +
            "from DiscussionComment d " +
            "where d.discussionTopic.topic_id = :topic_id and d.Create_Time>:date_time "+
            "order by d.Create_Time asc"
    )*/
    //애같은경우 orderby에 쓰인개 select likst에 들어가ㅑ된다 일바적으로 그게맞다 단 pageable를 같이쓴경우에는 없ㅎ어도무관.
    //그냥 웬만하면 넣어주도록하자 일관성잇게.
   // public List<DiscussionCommentDtos> get_comment_list3(@Param("topic_id") Long topic_id, @Param("date_time")LocalDateTime date_time);
}
