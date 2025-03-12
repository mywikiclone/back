package com.example.posttest.Init;


import com.example.posttest.dtos.RealTimeIssueDto;
import com.example.posttest.dtos.RealTimeIssueListDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisInit {

    private final RedisTemplate<String,String> redisTemplate;
    private final ObjectMapper objectMapper;




    @PostConstruct
    public void init(){
        try{
        RealTimeIssueDto realTimeIssueDto=new RealTimeIssueDto("testing",0L);
        ArrayList<RealTimeIssueDto> news=new ArrayList();
        news.add(realTimeIssueDto);
        redisTemplate.opsForValue().set("30secdata",objectMapper.writeValueAsString(news));}
        catch(Exception e){

            log.info("error:{}",e.getStackTrace());
        }

    }
}
