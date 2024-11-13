package com.example.posttest.etc.logininterceptors;

import com.example.posttest.Exceptions.AccessExceedError;
import com.example.posttest.Exceptions.EtcError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Time;
import java.util.concurrent.TimeUnit;


@Slf4j
@RequiredArgsConstructor
public class ExcessAccessInterCeptor implements HandlerInterceptor {



    private final RedisTemplate<String,String> redisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip=request.getRemoteAddr();

        ValueOperations<String,String> valueOperations =redisTemplate.opsForValue();
        String access_log=valueOperations.get(ip);
        Long ttl=redisTemplate.getExpire(ip);
        log.info("accecc_check:{}",access_log);
        log.info("check:{}",access_log==null);
        if(access_log==null){
            log.info("접속기록없음");


            valueOperations.set(ip,String.valueOf(1L),30L,TimeUnit.SECONDS);
            return true;
        }

        if(Long.parseLong( access_log)>=5L){

            valueOperations.set(ip,String.valueOf(5L),30L,TimeUnit.SECONDS);
            throw new AccessExceedError();
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
