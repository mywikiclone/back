package com.example.posttest.etc;

import com.example.posttest.dtos.MsgDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
@Slf4j
public class RedisSubPub {



    private final RedisTemplate<String,String> redisTemplate;
    private final ObjectMapper objectMapper;




    public void send_msg_to_msg_server(MsgDto msgDto) {
        try {
            String msg = objectMapper.writeValueAsString(msgDto);
            redisTemplate.convertAndSend("1", msg);

        }
        catch (Exception e){
            log.info("error:{}",e.getStackTrace());

        }
    }
}
