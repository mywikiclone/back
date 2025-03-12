package com.example.posttest.etc.logininterceptors;

import com.example.posttest.Exceptions.WebSocketError;
import io.netty.util.internal.SuppressJava6Requirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketSecureInterCeptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {


        StompHeaderAccessor stompHeaderAccessor=StompHeaderAccessor.wrap(message);

        log.info("stompheaderaccessor:{}",stompHeaderAccessor.getFirstNativeHeader("test"));


        if(stompHeaderAccessor.getFirstNativeHeader("test")!=null){
            log.info("웹소켓 아이디:{}",stompHeaderAccessor.getSessionId());
            throw new WebSocketError(stompHeaderAccessor.getSessionId());

            //여기서 발생한 에러는 stomperrorcontroller에서 캐치가 가능하다.
            //handleClientMessageProcessingError 구현한 이메서드에서 캐치하고 처리가 가능함.
            //물론 에러가 저기로 전달될떄 delivertfail error인가? 로 변경이되긴하는대 쩄든 에러를 잡을수가있게되었다.
           // return ErrorMessage("error");
        }
        log.info("여기를 통과했니혹시");
        return message;



    }


    public Message<String> ErrorMessage(String ErrorMsg){
        StompHeaderAccessor stompHeaderAccessor=StompHeaderAccessor.create(StompCommand.ERROR);
        stompHeaderAccessor.setMessage(ErrorMsg);


        return MessageBuilder.createMessage(ErrorMsg,stompHeaderAccessor.getMessageHeaders());
    }




}
