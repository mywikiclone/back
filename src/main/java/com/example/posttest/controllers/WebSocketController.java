package com.example.posttest.controllers;


import com.example.posttest.dtos.UserMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class WebSocketController {



    private final  SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/adminchatroom")
    @SendTo("/topic/adminchatroom")
    public UserMessage sendingmsg(UserMessage userMessage){






            return userMessage;

    }


    public void SendingAnotherEnvLoginMsg(String username) {
        String destination = "/topic/" + username;
        messagingTemplate.convertAndSend(destination,"다른 환경에서의 로그인 발생");
    }



}
