package com.example.posttest.etc.logininterceptors;

import com.example.posttest.Exceptions.CsrfError;
import com.example.posttest.Exceptions.ReLoginError;
import com.example.posttest.dtos.UserSession;
import com.example.posttest.dtos.UserSessionTot;
import com.example.posttest.etc.*;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;



import java.util.Arrays;
import java.util.Optional;


@RequiredArgsConstructor
@Slf4j

public class LoginInterceptor implements HandlerInterceptor {



    private final CookieRedisSession cookieRedisSession;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {



        if(isPreflightRequest(request)){


            return true;


        }


        UserSessionTot userSessionTot =cookieRedisSession.getusersessiontot(request);
        UserSession userSession=userSessionTot.getUserSession();


        if(userSession==null){

            throw new ReLoginError();

        }



        String token=request.getHeader("csrf-check");
        String csrf=userSession.getCrsf();

        if(csrf.equals(token)){


            return true;
        }

        else{



            throw new CsrfError();
        }


    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }


    private boolean isPreflightRequest(HttpServletRequest request) {
        return isOptions(request) && hasHeaders(request) && hasMethod(request) && hasOrigin(request);
    }

    private boolean isOptions(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.toString());
    }

    private boolean hasHeaders(HttpServletRequest request) {
        return (request.getHeader("Access-Control-Request-Headers"))!=null;
    }

    private boolean hasMethod(HttpServletRequest request) {
        return (request.getHeader("Access-Control-Request-Method"))!=null;
    }

    private boolean hasOrigin(HttpServletRequest request) {
        return (request.getHeader("Origin"))!=null;
    }




}






