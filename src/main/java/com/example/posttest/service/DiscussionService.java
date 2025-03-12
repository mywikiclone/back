package com.example.posttest.service;


import com.example.posttest.Exceptions.CantFindError;
import com.example.posttest.Exceptions.EtcError;
import com.example.posttest.Mapper.DiscussionMapper;
import com.example.posttest.Mapper.FeedMapper;
import com.example.posttest.dtos.*;
import com.example.posttest.entitiy.Content;
import com.example.posttest.entitiy.DiscussionComment;
import com.example.posttest.entitiy.DiscussionTopic;
import com.example.posttest.entitiy.Member;
import com.example.posttest.etc.*;
import com.example.posttest.repository.DiscussionCommentRepo;
import com.example.posttest.repository.DisscussionRepository;
import com.example.posttest.repository.FeedRepository;
import com.example.posttest.repository.contentrepositories.ContentRepository;
import com.example.posttest.repository.memrepo.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiscussionService {



    private final DisscussionRepository disscussionRepository;
    private final DiscussionCommentRepo discussionCommentRepo;
    private final ContentRepository contentRepository;
    private final MemberRepository memberRepository;
    private final CookieRedisSession cookieRedisSession;
    private final RedisSubPub redisSubPub;
    private final DiscussionMapper discussionMapper;
    private final ObjectMapper objectMapper;
    private final FeedMapper feedMapper;
    private final FeedService feedService;

    @Transactional
    public ResponseEntity<ApiResponse<DiscussionTopicDto>> savetopic(TopicDto topicDto, UserSessionTot userSessionTot){


        Long member_id=userSessionTot.getUserSession().getMember_id();

        LocalDateTime now=getlocldatetime();
        LocalDateTime deadline=now.plusMinutes(topicDto.getDeadline());
        Optional<Member> member=memberRepository.findById(member_id);

        Optional<Content> content=contentRepository.findbytitle(topicDto.getSubject_title());


        if(content.isPresent()){

            DiscussionTopic discussionTopic=new DiscussionTopic(member.get(),topicDto.getTopic_title(),content.get(),deadline,topicDto.getIntroduction_text());
            discussionTopic.setCreate_Time(now);
            DiscussionTopic discussionTopic1=disscussionRepository.save(discussionTopic);
            DiscussionTopicDto discussionTopicDto=new DiscussionTopicDto(member.get().getEmail(),topicDto.getTopic_title(),content.get().getTitle(),deadline,topicDto.getIntroduction_text(),discussionTopic1.getTopic_id());

            feedService.create_feed(member_id,discussionTopic1.getTopic_id());
            HttpHeaders headers=cookieRedisSession.makecookieinheader(userSessionTot,"extend");


            return new ResponseEntity(ApiResponse.success(discussionTopicDto, ErrorMsgandCode.Successupdate.getMsg()),headers,HttpStatus.OK);

        }
        throw new CantFindError();

    }


    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<TopicDto>>> gettopiclist(int page_num){
        Pageable page= PageRequest.of(page_num,3);

       Page<TopicDto> list= disscussionRepository.gettopics(page);


        if(list.isLast()){
            return ResponseEntity.ok(ApiResponse.success(list.stream().toList(),"마지막"));
        }


        //페이지에 해당되는값이없다면 0을 돌려줌.
       if(list.isEmpty()){

            List<TopicDto> list2=new ArrayList<>();

           return ResponseEntity.ok(ApiResponse.success(list2,"마지막"));
           //throw new CantFindError();
          // return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));
       }




       return ResponseEntity.ok(ApiResponse.success(list.stream().toList(),ErrorMsgandCode.Successfind.getMsg()));

    }




    public ResponseEntity<ApiResponse<DiscussionCommentDtos>> save_discussion_comment2(DiscussionCommentDtos dtos,UserSessionTot userSessionTot){

        Optional<DiscussionTopic> discussionTopic=disscussionRepository.findById(dtos.getTopic_id());
        Long member_id=userSessionTot.getUserSession().getMember_id();
        if(discussionTopic.isPresent()) {
            Optional<Member> member=memberRepository.findById(member_id);

            DiscussionComment discussionComment = new DiscussionComment(discussionTopic.get(),member.get(),dtos.getComment_content());
            LocalDateTime now=getlocldatetime();

            discussionComment.setCreate_Time(now);
            DiscussionComment discussionComment1=discussionCommentRepo.save(discussionComment);


            //List<Long> feed_list=discussionMapper.get_feed_list(dtos.getTopic_id(),member_id);

            List<Long> feed_list=feedMapper.get_feed_list(dtos.getTopic_id(),"OK",member_id);
            MsgDto msgDto=new MsgDto();
            try {
                msgDto = new MsgDto("feed_list", "1", discussionTopic.get().getTopic_id(), objectMapper.writeValueAsString(feed_list));
            }
            catch (Exception e){

            }
            redisSubPub.send_msg_to_msg_server(msgDto);

            DiscussionCommentDtos discussionCommentDtos=new DiscussionCommentDtos(dtos.getTopic_id(),member.get().getEmail(),member.get().getMember_id(),dtos.getComment_content(),discussionComment1.getCreate_Time());

            HttpHeaders headers=cookieRedisSession.makecookieinheader(userSessionTot,"extend");
            return new ResponseEntity(ApiResponse.success(discussionCommentDtos,ErrorMsgandCode.Successupdate.getMsg()),headers,HttpStatus.OK);

        }

        throw new EtcError();
        //return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failupdate.getMsg()));
    }



    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<DiscussionCommentDtos>>> get_discussion_comments(Long topic_id){

        List<DiscussionCommentDtos> lists=discussionCommentRepo.get_comment_list2(topic_id);


        if(lists.isEmpty()){



            List<DiscussionCommentDtos> list=new ArrayList<>();



            return ResponseEntity.ok(ApiResponse.success(list,"마지막"));
            //throw new CantFindError();




        }



        return ResponseEntity.ok(ApiResponse.success(lists.stream().toList(),ErrorMsgandCode.Successfind.getMsg()));

    }



    public ResponseEntity<ApiResponse<DiscussionTopicDto>> getdiscussion(Long topic_id){

            Optional<DiscussionTopic> discussionTopic=disscussionRepository.findById(topic_id);
            if(discussionTopic.isPresent()){
                return ResponseEntity.ok(ApiResponse.success(new DiscussionTopicDto(discussionTopic.get().getMember().getEmail(),discussionTopic.get().getTopic_title(),discussionTopic.get().getContent().getTitle(),discussionTopic.get().getDeadline(),discussionTopic.get().getIntroduction_text()),ErrorMsgandCode.Successfind.getMsg()));
            }

            throw new CantFindError();



    }


    public ResponseEntity<ApiResponse<String>> timeout(Long topic_id){
        Optional<DiscussionTopic> discussionTopic=disscussionRepository.findById(topic_id);
        LocalDateTime now=LocalDateTime.now();
        if(discussionTopic.isPresent()){
            if(now.isAfter(discussionTopic.get().getDeadline())){
                throw new CantFindError();




            }

            return ResponseEntity.ok(ApiResponse.success(ErrorMsgandCode.Successfind.getMsg(),ErrorMsgandCode.Successupdate.getMsg()));



        }


        throw new CantFindError();

    }







    public LocalDateTime getlocldatetime(){
        LocalDateTime now=LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        String now_string = now.format(formatter);
        return LocalDateTime.parse(now_string, formatter);
    }




}
