package com.example.posttest.etc.annotataion;

import com.example.posttest.Exceptions.ReLoginError;
import com.example.posttest.dtos.UserSession;
import com.example.posttest.dtos.UserSessionTot;
import com.example.posttest.etc.*;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


@RequiredArgsConstructor
@Slf4j
public class LoginAnnotationResolver implements HandlerMethodArgumentResolver {

    private final CookieRedisSession cookieRedisSession;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(LoginUser.class) !=null && parameter.getParameterType().equals(UserSessionTot.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest req=(HttpServletRequest) webRequest.getNativeRequest();
        UserSessionTot userSessionTot=cookieRedisSession.getusersessiontot(req);

        UserSession userSession=userSessionTot.getUserSession();


        return (UserSessionTot) userSessionTot;






    }
}
