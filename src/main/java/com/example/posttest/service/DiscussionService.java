package com.example.posttest.service;


import com.example.posttest.dtos.DiscussionCommentDtos;
import com.example.posttest.dtos.DiscussionTopicDto;
import com.example.posttest.dtos.TopicDto;
import com.example.posttest.entitiy.DiscussionComment;
import com.example.posttest.entitiy.DiscussionTopic;
import com.example.posttest.entitiy.Member;
import com.example.posttest.etc.ApiResponse;
import com.example.posttest.etc.ErrorMsgandCode;
import com.example.posttest.repository.DiscussionCommentRepo;
import com.example.posttest.repository.DisscussionRepository;
import com.example.posttest.repository.memrepo.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.loader.LoaderLogging;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiscussionService {



    private final DisscussionRepository disscussionRepository;
    private final DiscussionCommentRepo discussionCommentRepo;
    private final MemberRepository memberRepository;

    public ResponseEntity<ApiResponse<DiscussionTopicDto>> savetopic(TopicDto topicDto,Long member_id){



        LocalDateTime now=getlocldatetime();
        LocalDateTime deadline=now.plusMinutes(topicDto.getDeadline());
        Optional<Member> member=memberRepository.findById(member_id);
        DiscussionTopic discussionTopic=new DiscussionTopic(member_id,member.get().getEmail(),topicDto.getTopic_title(),topicDto.getSubject_title(),deadline,topicDto.getIntroduction_text());
        discussionTopic.setCreate_Time(now);
        DiscussionTopic discussionTopic1=disscussionRepository.save(discussionTopic);
        DiscussionTopicDto discussionTopicDto=new DiscussionTopicDto(member.get().getEmail(),topicDto.getTopic_title(),topicDto.getSubject_title(),deadline,topicDto.getIntroduction_text(),discussionTopic1.getTopic_id());


        return ResponseEntity.ok(ApiResponse.success(discussionTopicDto, ErrorMsgandCode.Successupdate.getMsg()));
    }

    public ResponseEntity<ApiResponse<List<TopicDto>>> gettopiclist(int page_num){
        Pageable page= PageRequest.of(page_num,3);

       Page<TopicDto> list= disscussionRepository.gettopics(page);
        //페이지에 해당되는값이없다면 0을 돌려줌.
       if(list.isEmpty()){


           return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));
       }

       return ResponseEntity.ok(ApiResponse.success(list.stream().toList(),ErrorMsgandCode.Successfind.getMsg()));

    }


    /*public Boolean save_discussion_comment(DiscussionCommentDtos dtos,Long member_id){

        Optional<DiscussionTopic> discussionTopic=disscussionRepository.findById(dtos.getTopic_id());

        if(discussionTopic.isPresent()) {
            Optional<Member> member=memberRepository.findById(member_id);
            DiscussionComment discussionComment = new DiscussionComment(discussionTopic.get(),member.get(),dtos.getComment_content());
            LocalDateTime now=getlocldatetime();
            discussionComment.setCreate_Time(now);
            DiscussionComment discussionComment1=discussionCommentRepo.save(discussionComment);



            DiscussionCommentDtos discussionCommentDtos=new DiscussionCommentDtos(dtos.getTopic_id(),member.get().getMember_id(),dtos.getComment_content(),discussionComment1.getCreate_Time());


            return true;

        }


        return false;
    }*/


    public ResponseEntity<ApiResponse<DiscussionCommentDtos>> save_discussion_comment2(DiscussionCommentDtos dtos,Long member_id){

        Optional<DiscussionTopic> discussionTopic=disscussionRepository.findById(dtos.getTopic_id());

        if(discussionTopic.isPresent()) {
            Optional<Member> member=memberRepository.findById(member_id);
            log.info("존재하는가:?{}{}",discussionTopic.isPresent(),member.isPresent());
            DiscussionComment discussionComment = new DiscussionComment(discussionTopic.get(),member.get(),dtos.getComment_content());
            LocalDateTime now=getlocldatetime();
            log.info("now값:{}",now);
            discussionComment.setCreate_Time(now);
            DiscussionComment discussionComment1=discussionCommentRepo.save(discussionComment);



            DiscussionCommentDtos discussionCommentDtos=new DiscussionCommentDtos(dtos.getTopic_id(),member.get().getEmail(),member.get().getMember_id(),dtos.getComment_content(),discussionComment1.getCreate_Time());


            return ResponseEntity.ok(ApiResponse.success(discussionCommentDtos,ErrorMsgandCode.Successupdate.getMsg()));

        }


        return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failupdate.getMsg()));
    }


   /* public ResponseEntity<ApiResponse<List<DiscussionCommentDtos>>> get_discussion_comments(Long topic_id,int page_num){
        Pageable page= PageRequest.of(page_num,3);
        Page<DiscussionCommentDtos> comment_list=discussionCommentRepo.get_comment_list(topic_id,page);



        if(comment_list.isEmpty()){


            return ResponseEntity.ok(ApiResponse.fail("실패"));



        }



        return ResponseEntity.ok(ApiResponse.success(comment_list.stream().toList(),ErrorMsgandCode.Successfind.getMsg()));

    }*/


    public ResponseEntity<ApiResponse<List<DiscussionCommentDtos>>> get_discussion_comments(Long topic_id){

        List<DiscussionCommentDtos> lists=discussionCommentRepo.get_comment_list2(topic_id);


        if(lists.isEmpty()){


            return ResponseEntity.ok(ApiResponse.fail("실패"));



        }



        return ResponseEntity.ok(ApiResponse.success(lists.stream().toList(),ErrorMsgandCode.Successfind.getMsg()));

    }


   /* public ApiResponse<List<DiscussionCommentDtos>> get_discussion_comments(Long topic_id,LocalDateTime datetime){

        List<DiscussionCommentDtos> lists=discussionCommentRepo.get_comment_list3(topic_id,datetime);


        if(lists.isEmpty()){


            return ApiResponse.fail("실패");



        }



        return ApiResponse.success(lists.stream().toList(),ErrorMsgandCode.Successfind.getMsg());




    }*/

    public ResponseEntity<ApiResponse<DiscussionTopicDto>> getdiscussion(Long topic_id){

            Optional<DiscussionTopic> discussionTopic=disscussionRepository.findById(topic_id);
            if(discussionTopic.isPresent()){
                return ResponseEntity.ok(ApiResponse.success(new DiscussionTopicDto(discussionTopic.get().getWriter_email(),discussionTopic.get().getTopic_title(),discussionTopic.get().getSubject_title(),discussionTopic.get().getDeadline(),discussionTopic.get().getIntroduction_text()),ErrorMsgandCode.Successfind.getMsg()));
            }


            return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));


    }


    public ResponseEntity<ApiResponse<String>> timeout(Long topic_id){
        Optional<DiscussionTopic> discussionTopic=disscussionRepository.findById(topic_id);
        LocalDateTime now=LocalDateTime.now();
        if(discussionTopic.isPresent()){
            log.info("시간체크:{}",now.isAfter(discussionTopic.get().getDeadline()));
            if(now.isAfter(discussionTopic.get().getDeadline())){

                return ResponseEntity.ok(ApiResponse.success(ErrorMsgandCode.Successfind.getMsg(),ErrorMsgandCode.Successupdate.getMsg()));


            }

            return ResponseEntity.ok(ApiResponse.fail("false"));


        }


        return ResponseEntity.ok(ApiResponse.fail("false"));
    }







    public LocalDateTime getlocldatetime(){
        LocalDateTime now=LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        String now_string=now.format(formatter);
        return LocalDateTime.parse(now_string,formatter);
    }




}
