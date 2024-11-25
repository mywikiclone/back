package com.example.posttest.etc.logininterceptors;

import com.example.posttest.Exceptions.CsrfError;
import com.example.posttest.Exceptions.EtcError;
import com.example.posttest.Exceptions.ReLoginError;
import com.example.posttest.dtos.UserSession;
import com.example.posttest.dtos.UserSessionTot;
import com.example.posttest.etc.CookieRedisSession;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class HandShakingInterceptor implements HandshakeInterceptor {


    private final CookieRedisSession cookieRedisSession;
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        HttpServletRequest req=(HttpServletRequest) request;

        UserSessionTot userSessionTot =cookieRedisSession.getusersessiontot(req);
        UserSession userSession=userSessionTot.getUserSession();

        log.info("cookie:{}",userSessionTot.getCookie().getValue());
        String token=req.getHeader("Csrf_check");
        log.info("csrf:{}",token);
        return true;
        /*if(userSession==null){

            throw new EtcError();

        }

        String token=req.getHeader("Csrf_check");
        //String csrf=(String) httpSession.getAttribute("csrf");
        String csrf=userSession.getCrsf();
        if(csrf.equals(token)){


            return true;
        }

        else{

            cookieRedisSession.delete_user_session_tot(userSessionTot);

            throw new EtcError();
        }*/






    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
