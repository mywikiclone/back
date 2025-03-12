package com.example.posttest.Config;

import com.example.posttest.etc.CookieRedisSession;
import com.example.posttest.etc.annotataion.LoginAnnotationResolver;

import com.example.posttest.etc.logininterceptors.LoginInterceptor;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import java.util.List;


@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CookieRedisSession cookieRedisSession;
    private final RedisTemplate<String,String> redisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor(cookieRedisSession))
                .order(1)
                .addPathPatterns("/update", "/save", "/admin/**", "/topicsave", "/savecomment", "/changepassword");


    }







    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginAnnotationResolver(cookieRedisSession));

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:3000","https://mywikifront.mywikiback.shop")
                .allowedMethods("*")
                .allowedHeaders("*")//헤더도 이런설정이있따 ㅇㅇ;'
                .exposedHeaders("Csrf_check")//z클라이언트가 응답을볼떄 볼수잇는 헤더지정
                .allowCredentials(true);
    }





}
