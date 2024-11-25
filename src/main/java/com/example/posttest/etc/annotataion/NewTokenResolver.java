package com.example.posttest.etc.annotataion;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;



@Slf4j
public class NewTokenResolver implements HandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CheckNewToken.class)!=null && parameter.getParameterType().equals(String.class);}

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest httpServletRequest=(HttpServletRequest) webRequest.getNativeRequest();

        if(httpServletRequest.getAttribute("ReGenToken")!=null){

            return httpServletRequest.getAttribute("ReGenToken");
        }


        return null;
    }
}
