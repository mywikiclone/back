package com.example.posttest.controllers;

import com.example.posttest.dtos.DiscussionCommentDtos;
import com.example.posttest.dtos.DiscussionTopicDto;
import com.example.posttest.dtos.TopicDto;
import com.example.posttest.entitiy.DiscussionComment;
import com.example.posttest.entitiy.DiscussionTopic;
import com.example.posttest.etc.ApiResponse;
import com.example.posttest.etc.ErrorMsgandCode;
import com.example.posttest.etc.JwtToken;
import com.example.posttest.etc.JwtUtil;
import com.example.posttest.etc.annotataion.CheckNewToken;
import com.example.posttest.etc.annotataion.LoginUser;
import com.example.posttest.service.DiscussionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DiscussionController {


    private final DiscussionService discussionService;


    private final JwtUtil jwtUtil;


    @Value("${spring.jwt.expiration}")
    private  Long expiration;

    @PostMapping("/topicsave")
    public ResponseEntity<ApiResponse<DiscussionTopicDto>> savetopic(@LoginUser Long member_id, @RequestBody TopicDto topicDto/*, @CheckNewToken String newtoken*/){

        //return ResponseEntity.ok(discussionService.savetopic(topicDto,member_id));

        return discussionService.savetopic(topicDto,member_id);

       /* long memberid=optionalnumscheck(member_id,newtoken);
        log.info("memberid:{}",memberid);

        return return_ans_method(discussionService.savetopic(topicDto,memberid),newtoken);*/

    }
    /*@PostMapping("/savecomment/{topic_id}/{date}")
    public ResponseEntity<ApiResponse<List<DiscussionCommentDtos>>> savecomment(@LoginUser Long member_id, @RequestBody DiscussionCommentDtos dtos,@CheckNewToken String newtoken,@PathVariable("topic_id")Long topic_id,@PathVariable("date") String date){

        date= URLDecoder.decode(date, StandardCharsets.UTF_8);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

        if(26l>date.length()){
            log.info("길이가안맞음:{}",date);
            Long nums=26l-date.length();
            log.info("nums:{}",nums);
            for(int x=0;nums>x;x++){

                date+="0";//0넣어주면 아래에서 파싱할떄 사라지내,,?
                log.info("data:{}",date);
            }


            log.info("길이수정:{}",date);
        }

        LocalDateTime dates=LocalDateTime.parse(date,formatter);
        if(discussionService.save_discussion_comment(dtos,member_id)){


            return return_ans_method(discussionService.get_discussion_comments(dtos.getTopic_id(),dates),newtoken);

        }
        return return_ans_method(ApiResponse.fail(ErrorMsgandCode.Failupdate.getMsg()),newtoken);



        //return return_ans_method(discussionService.save_discussion_comment(dtos,1L),newtoken);



    }*/


    @PostMapping("/savecomment")
    public ResponseEntity<ApiResponse<DiscussionCommentDtos>> savecomment2(@LoginUser Long member_id, @RequestBody DiscussionCommentDtos dtos/*,@CheckNewToken String newtoken*/){



        return discussionService.save_discussion_comment2(dtos,member_id);

        /*long memberid=optionalnumscheck(member_id,newtoken);
        return return_ans_method(discussionService.save_discussion_comment2(dtos,memberid),newtoken);*/





    }






    @GetMapping("/topiclist/{page}")
    public ResponseEntity<ApiResponse<List<TopicDto>>> gettopiclist(@PathVariable("page") int page){



        return discussionService.gettopiclist(page);


    }


    /*@GetMapping("/getcomments/{topic_id}/{page_num}")
    public ResponseEntity<ApiResponse<List<DiscussionCommentDtos>>> getcomments(@PathVariable("topic_id") Long topic_id,@PathVariable("page_num")int page_num){


        return discussionService.get_discussion_comments(topic_id,page_num);
    }*/

    @GetMapping("/getcomments/{topic_id}")
    public ResponseEntity<ApiResponse<List<DiscussionCommentDtos>>> getcomments2(@PathVariable("topic_id") Long topic_id){


        return discussionService.get_discussion_comments(topic_id);
    }

    @GetMapping("/getdiscussion/{topic_id}")
    public ResponseEntity<ApiResponse<DiscussionTopicDto>> getdiscussion(@PathVariable("topic_id") Long topic_id){


            return discussionService.getdiscussion(topic_id);

    }


    @GetMapping("/timecheck/{topic_id}")
    public ResponseEntity<ApiResponse<String>> gettimeout(@PathVariable("topic_id")Long topic_id){


        return discussionService.timeout(topic_id);

    }




    public <T>ResponseEntity<ApiResponse<T>> return_ans_method(ApiResponse<T> apiResponse,String token){
        if(token==null){

            return ResponseEntity.ok(apiResponse);
        }

        ResponseCookie responseCookie=ResponseCookie.from("back_access_token",token)
                .maxAge(expiration/1000)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain("localhost")
                .build();
        HttpHeaders headers=new HttpHeaders();

        headers.add(HttpHeaders.SET_COOKIE,responseCookie.toString());

        return new ResponseEntity<>(apiResponse,headers, HttpStatus.OK);
    }

    public long optionalnumscheck(Long num, String newtoken){

        if(num!=null){

            return num;





        }


        return jwtUtil.getidfromtoken(newtoken);

    }


}
