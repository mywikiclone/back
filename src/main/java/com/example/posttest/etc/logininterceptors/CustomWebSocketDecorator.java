package com.example.posttest.etc.logininterceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
public class CustomWebSocketDecorator extends WebSocketHandlerDecorator {


    private final ConcurrentHashMap<String,WebSocketSession> sessionmap=new ConcurrentHashMap<>();

    public CustomWebSocketDecorator(@Qualifier("customhandler") WebSocketHandler delegate) {
        super(delegate);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("소켓맵에 추가:{}",session.getId());
        sessionmap.put(session.getId(),session);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("소켓맵에서 제거:{}",session.getId());
        sessionmap.remove(session.getId());
        super.afterConnectionClosed(session, closeStatus);
    }



    public void closesession(String sessionid) throws IOException {
        WebSocketSession webSocketSession=sessionmap.get(sessionid);
        log.info("closesession함수");
        if(webSocketSession!=null&&webSocketSession.isOpen()){

            webSocketSession.close();
        }


    }
}
