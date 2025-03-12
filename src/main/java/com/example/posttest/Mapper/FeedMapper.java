package com.example.posttest.Mapper;


import com.example.posttest.dtos.MemberFeedDto;
import com.example.posttest.entitiy.MemberFeed;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FeedMapper {



    Optional<MemberFeedDto> getmember_feed(@Param("member_id") Long member_id, @Param("topic_id") Long topic_id);



    int update_feed(@Param("state") String state,@Param("member_id") Long member_id, @Param("topic_id") Long topic_id);



    List<Long> get_feed_list(@Param("topic_id") Long topic_id,@Param("feed_check") String feed_check,@Param("member_id") Long member_id);
}
