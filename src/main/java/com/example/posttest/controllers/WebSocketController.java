package com.example.posttest.controllers;


import com.example.posttest.Exceptions.WebSocketError;
import com.example.posttest.dtos.UserMessage;
import com.example.posttest.etc.logininterceptors.CustomWebSocketDecorator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.io.IOException;

@Controller
@Slf4j
@RequiredArgsConstructor
public class WebSocketController {



    private final  SimpMessagingTemplate messagingTemplate;
    private final CustomWebSocketDecorator customWebSocketDecorator;

    @MessageMapping("/adminchatroom")
    @SendTo("/topic/adminchatroom")
    public UserMessage sendingmsg(UserMessage userMessage) {


        log.info("메시지도착함");

        if(true){
        throw new RuntimeException();}
        return userMessage;

    }



    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String errormesg(Exception ex, Message msg) throws IOException {
        StompHeaderAccessor stompHeaderAccessor=StompHeaderAccessor.wrap(msg);
        log.info("에러메시지 내용까보기:{}",ex.getMessage());
        log.info("msg:{}",stompHeaderAccessor.getSessionId());
        //변수로 message객체를 받기도 가능하다.
        //customWebSocketDecorator.closesession(ex.getMessage());

        return "사용자 에러발생";
    }


    public void SendingAnotherEnvLoginMsg(String username) {

        String destination = "/topic/" + username;

        UserMessage userMessage=new UserMessage("ME","ADSADSADSAD");
        messagingTemplate.convertAndSend(destination,userMessage);
    }








}
