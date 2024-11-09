package com.example.posttest.Config;

import com.example.posttest.etc.JwtUtil;
import com.example.posttest.etc.annotataion.LoginAnnotationResolver;
import com.example.posttest.etc.annotataion.NewTokenResolver;
import com.example.posttest.etc.filter.CookieFilter;
import com.example.posttest.etc.logininterceptors.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
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
                .addPathPatterns("/update","/save","/admin/**","/topicsave","/savecomment","/changepassword");






    }
    @Bean
    public FilterRegistrationBean sameSiteCookieFilter() {
        FilterRegistrationBean<CookieFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CookieFilter());
        registrationBean.addUrlPatterns("/firlogin"); // 모든 URL에 적용
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginAnnotationResolver(jwtUtil));
        resolvers.add(new NewTokenResolver());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:3000","http://ec2-54-180-237-28.ap-northeast-2.compute.amazonaws.com\n")
                .allowedMethods("*")
                .allowedHeaders("*")//헤더도 이런설정이있따 ㅇㅇ;'
                .exposedHeaders("Csrf_Check","Csrf_check")//z클라이언트가 응답을볼떄 볼수잇는 헤더지정
                .allowCredentials(true);
    }



}
