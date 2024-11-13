package com.example.posttest.etc.logininterceptors;

import com.example.posttest.Exceptions.CsrfError;
import com.example.posttest.Exceptions.ReLoginError;
import com.example.posttest.etc.JwtToken;
import com.example.posttest.etc.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j

public class LoginInterceptor implements HandlerInterceptor {


    private final JwtUtil jwtUtil;
    private final RedisTemplate<String,String> redisTemplate;
    @Value("${spring.jwt.expiration}")
    private  Long expiration;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {





        log.info("===========인터셉터호출==============");
        log.info("controller:{}",handler.getClass());
        log.info("들어온 경로:{}",request.getRequestURI());
        log.info("request객체설명:{}",request.getHeader("Content-Type"));




        if(isPreflightRequest(request)){

            log.info("preflightrequest입니다!!!!!!!!!!!!!!");
            return true;


        }


        HttpSession httpSession= request.getSession(false);


        if(httpSession==null){

            throw new ReLoginError();

        }

        String token=request.getHeader("Csrf_check");
        String csrf=(String) httpSession.getAttribute("csrf");
        log.info("csrf:{} {}  {}",csrf,token,csrf.equals(token));
        if(csrf.equals(token)){


            return true;
        }

        else{

            httpSession.invalidate();

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

    private Long check_refresh_token_valid(HttpServletRequest req){
        Cookie[] cookies=req.getCookies();
        if(cookies!=null){
        Optional<Cookie> cookie= Arrays.stream(cookies).filter(x->"back_refresh_token".equals(x.getName()))
                .findFirst();

        Optional<String> number=Optional.ofNullable((String)redisTemplate.opsForValue().get(cookie.get().getValue()));
        if(cookie.isPresent()&&number.isPresent()) {



                return Long.parseLong(number.get());




        }
        else {
            throw new ReLoginError();
        }
        }
        else {
            throw new ReLoginError();
        }
    }
}






