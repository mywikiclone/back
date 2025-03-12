package com.example.posttest.service;


import com.example.posttest.Mapper.DiscussionMapper;
import com.example.posttest.Mapper.FeedMapper;
import com.example.posttest.dtos.MemberFeedDto;
import com.example.posttest.entitiy.DiscussionTopic;
import com.example.posttest.entitiy.Member;
import com.example.posttest.entitiy.MemberFeed;
import com.example.posttest.etc.ApiResponse;
import com.example.posttest.etc.FeedEnum;
import com.example.posttest.repository.DisscussionRepository;
import com.example.posttest.repository.FeedRepository;
import com.example.posttest.repository.memrepo.MemberRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {


    private final FeedMapper feedMapper;
    private final MemberRepository memberRepository;
    private final DisscussionRepository disscussionRepository;
    private final FeedRepository feedRepository;



    @Transactional
    public String makedata(){


        Optional<MemberFeed> memberFeed=feedRepository.findById(1L);
        log.info("logs:{}",memberFeed.get().getFeedEnum());

        return "ok";
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> create_feed(Long member_id,Long topic_id){



        Optional<MemberFeedDto> memberFeed=feedMapper.getmember_feed(member_id,topic_id);
        log.info("member_feed_eixist:{} {}",memberFeed.isPresent(),FeedEnum.OK.getState());

        if(memberFeed.isPresent()){

            feedMapper.update_feed(FeedEnum.OK.getState(),member_id,topic_id);

            return ResponseEntity.ok(ApiResponse.success("OK","HELLO"));
        }

        Member member=memberRepository.findById(member_id).get();

        DiscussionTopic discussionTopic=disscussionRepository.findById(topic_id).get();

        MemberFeed memberFeed1=new MemberFeed(member,discussionTopic,FeedEnum.OK);

        feedRepository.save(memberFeed1);


        return ResponseEntity.ok(ApiResponse.success("OK","HELLO"));
    }




    @Transactional
    public ResponseEntity<ApiResponse<String>> cancel_feed(Long member_id,Long topic_id){

        feedMapper.update_feed(FeedEnum.NOT.getState(),member_id,topic_id);

        return ResponseEntity.ok(ApiResponse.success("OK","HELLO"));


    }


    public ResponseEntity<ApiResponse<String>> get_feed(Long member_id, Long topic_id){


        Optional<MemberFeedDto> memberFeed=feedMapper.getmember_feed(member_id,topic_id);
        log.info("feed:{}",memberFeed.isPresent());


        if(memberFeed.isPresent()&&memberFeed.get().getFeed_check().equals("OK")){

         return ResponseEntity.ok(ApiResponse.success("OK","HELLO"));


        }



        return ResponseEntity.ok(ApiResponse.success("NOT","HELLO"));


    }







}
