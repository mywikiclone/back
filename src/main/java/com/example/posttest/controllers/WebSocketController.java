package com.example.posttest.controllers;


import com.example.posttest.dtos.UserMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class WebSocketController {




    @MessageMapping("/adminchatroom")
    @SendTo("/topic/adminchatroom")
    public UserMessage sendingmsg(UserMessage userMessage, SimpMessageHeaderAccessor headerAccessor){

            log.info("sessionid:{}",headerAccessor.getSessionId());




            return userMessage;

    }



}
