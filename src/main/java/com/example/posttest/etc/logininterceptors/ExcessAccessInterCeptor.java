package com.example.posttest.etc.logininterceptors;

import com.example.posttest.Exceptions.AccessExceedError;

import com.example.posttest.dtos.MemberDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.util.concurrent.TimeUnit;


@Slf4j
@RequiredArgsConstructor
public class ExcessAccessInterCeptor implements HandlerInterceptor {




    private final RedisTemplate<String,String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        /*StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        // 2. Gson 객체를 사용하여 JSON을 Java 객체로 변환
        String jsonString = sb.toString();*/

        String ip=request.getRemoteAddr();

        ValueOperations<String,String> valueOperations =redisTemplate.opsForValue();
        String access_log=valueOperations.get(ip);
        Long ttl=redisTemplate.getExpire(ip);

        if(access_log==null){
            log.info("접속기록없음");


            valueOperations.set(ip,String.valueOf(1L),30L,TimeUnit.SECONDS);
            return true;
        }

        if(Long.parseLong( access_log)>=5L){

            valueOperations.set(ip,String.valueOf(5L),30L,TimeUnit.SECONDS);
            throw new AccessExceedError("zzz","zzz");
        }
        if(ttl>0L) {


            valueOperations.set(ip, String.valueOf(Long.parseLong(  access_log) + 1L), ttl, TimeUnit.SECONDS);
            return true;
        }

        valueOperations.set(ip, String.valueOf( 1L),30L, TimeUnit.SECONDS);

        return true;



    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
