package com.example.posttest.etc.annotataion;

import com.example.posttest.Exceptions.ReLoginError;
import com.example.posttest.etc.JwtUtil;
import com.example.posttest.etc.LoginSessionConst;
import com.example.posttest.etc.annotataion.LoginUser;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class LoginAnnotationResolver implements HandlerMethodArgumentResolver {



    private final JwtUtil jwtUtil;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(LoginUser.class) !=null && parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest req=(HttpServletRequest) webRequest.getNativeRequest();
        log.info("어노테이션작동?");


        HttpSession httpSession= req.getSession(false);
        log.info("is it:{}?",httpSession==null);
        if(httpSession==null){
            throw new ReLoginError();
        }


        return (Long) httpSession.getAttribute(LoginSessionConst.session_const);


        /*if(req.getAttribute("ReGenToken")!=null){

            log.info("없으니까 재발급 토큰을 사용");
            return jwtUtil.getidfromtoken((String) req.getAttribute("ReGenToken"));




        }
        Cookie[] cookies = req.getCookies();
        log.info("쿠키가있나:{}",cookies);
        Optional<Cookie> cookie = Arrays.stream(cookies).filter(x -> "back_access_token".equals(x.getName()))
                    .findFirst();


        if(cookie.isEmpty()){



            return null;
        }

        return jwtUtil.getidfromtoken(cookie.get().getValue());*/





    }
}
