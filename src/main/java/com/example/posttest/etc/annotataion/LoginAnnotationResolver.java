package com.example.posttest.etc.annotataion;

import com.example.posttest.etc.JwtUtil;
import com.example.posttest.etc.annotataion.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class LoginAnnotationResolver implements HandlerMethodArgumentResolver {



    private final JwtUtil jwtUtil;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(LoginUser.class) !=null && parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest req=(HttpServletRequest) webRequest.getNativeRequest();
        String access_token=req.getHeader("Authorization");
        access_token=access_token.substring(7);

        return jwtUtil.getidfromtoken(access_token);
    }
}
