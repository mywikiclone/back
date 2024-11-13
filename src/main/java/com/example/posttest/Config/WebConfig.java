package com.example.posttest.Config;

import com.example.posttest.etc.JwtUtil;
import com.example.posttest.etc.annotataion.LoginAnnotationResolver;
import com.example.posttest.etc.annotataion.NewTokenResolver;
import com.example.posttest.etc.filter.CookieFilter;
import com.example.posttest.etc.logininterceptors.ExcessAccessInterCeptor;
import com.example.posttest.etc.logininterceptors.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.http.CookieProcessorBase;
import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
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
        registry.addInterceptor(new LoginInterceptor(jwtUtil, redisTemplate))
                .order(2)
                .addPathPatterns("/update", "/save", "/admin/**", "/topicsave", "/savecomment", "/changepassword");


        registry.addInterceptor(new ExcessAccessInterCeptor(redisTemplate))
                .order(1)
                .addPathPatterns("/firlogin");


    }


    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieProcessorCustomizer() {
        return (factory) -> factory.addContextCustomizers(

                (context) -> context.setCookieProcessor(new LegacyCookieProcessor()));
    }


    /*@Bean
    public WebServerFactoryCustomizer<TomcatWebServer> tomcatCustomizer() {
        return factory -> {
            if (factory instanceof TomcatWebServer) {
                Tomcat tomcat = ((TomcatWebServer) factory).getTomcat();


                tomcat.getEngine().setCookieProcessor(new LegacyCookieProcessor());
            }
        };
    }*/



    /*@Bean
    public EmbeddedServletContainerCustomizer tomcatCustomizer() {
        return container -> {
            if (container instanceof TomcatEmbeddedServletContainerFactory) {
                TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
                tomcat.addContextCustomizers(context -> context.setCookieProcessor(new LegacyCookieProcessor()));
            }
        };
    }*/






    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginAnnotationResolver(jwtUtil));
        resolvers.add(new NewTokenResolver());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:3000","https://mywikifront.mywikiback.shop")
                .allowedMethods("*")
                .allowedHeaders("*")//헤더도 이런설정이있따 ㅇㅇ;'
                .exposedHeaders("Csrf_Check","Csrf_check")//z클라이언트가 응답을볼떄 볼수잇는 헤더지정
                .allowCredentials(true);
    }



}
