package com.example.posttest.Config;

import com.example.posttest.etc.JwtUtil;
import com.example.posttest.etc.annotataion.LoginAnnotationResolver;
import com.example.posttest.etc.annotataion.NewTokenResolver;
import com.example.posttest.etc.logininterceptor.LoginInterceptor;
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

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String,String> redisTemplate;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor(jwtUtil,redisTemplate))
                .order(1)
                .addPathPatterns("/update","/changelog/**");
                //.excludePathPatterns("/**")



    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginAnnotationResolver(jwtUtil));
        resolvers.add(new NewTokenResolver());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")//헤더도 이런설정이있따 ㅇㅇ;'
                .allowCredentials(true);
    }
}
