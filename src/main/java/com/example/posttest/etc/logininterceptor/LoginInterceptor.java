package com.example.posttest.etc.logininterceptor;

import com.example.posttest.Exceptions.ReLoginError;
import com.example.posttest.etc.JwtToken;
import com.example.posttest.etc.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j

public class LoginInterceptor implements HandlerInterceptor {


    private final JwtUtil jwtUtil;
    private final RedisTemplate<String,String> redisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        log.info("===========인터셉터호출==============");
        log.info("controller:{}",handler.getClass());
        log.info("들어온 경로:{}",request.getRequestURI());
        log.info("request객체설명:{}",request.getHeader("Content-Type"));




        String access_token=request.getHeader("Authorization").substring(7);


        try{
            return jwtUtil.validatetoken(access_token);
        }
        catch(ExpiredJwtException e){
            log.info("error token:{}",access_token);
            if(redisTemplate.opsForValue().get(access_token)==null){

                throw new ReLoginError();
            }



            Long ttl_left=redisTemplate.getExpire(access_token);





            Long user_id=jwtUtil.getidfromtoken_decode_payload(access_token);

            JwtToken jwtToken=jwtUtil.genjwt(user_id);

            String refresh_token=(String) redisTemplate.opsForValue().get(access_token);
            log.info("refresh_token:{}",refresh_token);

            redisTemplate.delete(access_token);

            redisTemplate.opsForValue().set(jwtToken.getAccesstoken(),refresh_token,ttl_left,TimeUnit.SECONDS);


            request.setAttribute("ReGenToken",jwtToken.getAccesstoken());

            return true;
        }




        //return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("aftercompletion controller    :{}",handler.getClass());
        log.info("=====인터셉터종료======");
    }
}
