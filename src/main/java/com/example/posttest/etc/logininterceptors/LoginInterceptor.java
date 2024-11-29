package com.example.posttest.etc.logininterceptors;

import com.example.posttest.Exceptions.CsrfError;
import com.example.posttest.Exceptions.ReLoginError;
import com.example.posttest.dtos.UserSession;
import com.example.posttest.dtos.UserSessionTot;
import com.example.posttest.etc.*;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;



import java.util.Arrays;
import java.util.Optional;


@RequiredArgsConstructor
@Slf4j

public class LoginInterceptor implements HandlerInterceptor {



    private final CookieRedisSession cookieRedisSession;
    @Value("${spring.jwt.expiration}")
    private  Long expiration;



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {




        log.info("===========인터셉터호출==============");
        log.info("controller:{}",handler.getClass());
        log.info("들어온 경로:{}",request.getRequestURI());
        log.info("request객체설명:{}",request.getHeader("Content-Type"));
        log.info("x-forwarded:{}",request.getHeader("X-Forwarded-For"));



        if(isPreflightRequest(request)){

            log.info("preflightrequest입니다!!!!!!!!!!!!!!");
            return true;


        }


        UserSessionTot userSessionTot =cookieRedisSession.getusersessiontot(request);
        UserSession userSession=userSessionTot.getUserSession();
        log.info("usesession:{}",userSession);
       // HttpSession httpSession= request.getSession(false);


        if(userSession==null){

            throw new ReLoginError();

        }



        log.info("token:{}",request.getHeader("Csrf_check"));
        log.info("token2:{}",request.getHeader("Csrf_Check"));
        String token=request.getHeader("Csrf_check");
        String csrf=userSession.getCrsf();
        log.info("token:{}",token);
        log.info("csrf:{}",csrf);
        if(csrf.equals(token)){


            return true;
        }

        else{

            //cookieRedisSession.delete_user_session_tot(userSessionTot);

            throw new CsrfError();
        }


    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }


    private boolean isPreflightRequest(HttpServletRequest request) {
        return isOptions(request) && hasHeaders(request) && hasMethod(request) && hasOrigin(request);
    }

    private boolean isOptions(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.toString());
    }

    private boolean hasHeaders(HttpServletRequest request) {
        return (request.getHeader("Access-Control-Request-Headers"))!=null;
    }

    private boolean hasMethod(HttpServletRequest request) {
        return (request.getHeader("Access-Control-Request-Method"))!=null;
    }

    private boolean hasOrigin(HttpServletRequest request) {
        return (request.getHeader("Origin"))!=null;
    }


    private Optional<String> get_token_from_req(HttpServletRequest req){

        Cookie[] cookies=req.getCookies();
        if(cookies!=null){




        Optional<Cookie> cookie= Arrays.stream(cookies).filter(x->"back_access_token".equals(x.getName()))
                .findFirst();
        if(cookie.isEmpty()){
            return Optional.empty();
        }


        Optional<String> access_token=Optional.ofNullable(cookie.get().getValue());


        return access_token;}
        return Optional.empty();
    }


}






