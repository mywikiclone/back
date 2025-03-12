package com.example.posttest.Config;

import com.example.posttest.Exceptions.WebSocketError;
import com.example.posttest.etc.CookieRedisSession;
import com.example.posttest.etc.logininterceptors.CustomWebSocketDecorator;
import com.example.posttest.etc.logininterceptors.StompErrorController;
import com.example.posttest.etc.logininterceptors.WebSocketSecureInterCeptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.io.IOException;


@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    private final CookieRedisSession cookieRedisSession;

    private final WebSocketSecureInterCeptor webSocketSecureInterCeptor;


    private final StompErrorController stompErrorHandler;



    @Bean
    public WebSocketHandlerDecorator customhandler(@Qualifier("subProtocolWebSocketHandler") WebSocketHandler webSocketHandler){


        return new CustomWebSocketDecorator(webSocketHandler);
    }
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
       registry.addDecoratorFactory(this::customhandler);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic","/queue");
        //클라->서버에서보낼때 /topic/~~ 이런식으로 보낸다이말
        registry.setApplicationDestinationPrefixes("/app");
        //이건 서버->클라로 /app/~~를 구독한애들 한태 뿌린다는말
        //무슨 원린지는 모르겟는대 웹소켓 연결이 설정되면은하여튼간에 클라이언트를 식별할수가있다.
    }
    //stomp란것은 websocket이라는 통신방식에서 http의 request,response처럼 양식을 정해주는 애이다.
    //즉 stomp는 단순 웹소케산의 전유물이아니라 다른 프로토콜 방식에서도 쓸수잇는 메시지 전달 프로토콜이다.
    //구체적으로는 websocket의 경우 메시지 ㄷ전달에서 http의 헤더 바디같은 규격이 없다 그러나
    //stomp를 쓸경우 헤더 바디같은 규격이 생겨나며 특히 여기서사용하고있는 구독 이라는 개념은 stomp라는 프로토콜에 존재하는 개념이다.
    //즉 websocket이라는 실시간 통신 방식과 stomp의 구독개념이 합쳐져서 작동하는게 내가만든것.
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")//이건 처음에 http통신으로 핸드쎼이킹시 클라이언트에서 서버로 요청을보낼
                //엔드포인트를 말한다.
                //.setAllowedOrigins("*")

                .setAllowedOrigins("http://localhost:3000","https://mywikifront.mywikiback.shop","http://localhost:8080")//처음에 http로 진행되는 핸드셰이킹 과정에서만 성립함.

                //이건 핸드셰이킹 과정 즉 http통신과정에 작용하는 인터셉터
                //참고ㅓ로 websocket연결 즉 핸드셰이킹 이후에는 http통신이아니므로 헤더 혹은 쿠키첨부ㅏ 불가능해진다.
                //.addInterceptors(new HandShakingInterceptor(cookieRedisSession))
                .withSockJS();
        registry.setErrorHandler(stompErrorHandler);

    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
       registration.interceptors(webSocketSecureInterCeptor);
    }




}
