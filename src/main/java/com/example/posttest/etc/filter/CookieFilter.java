package com.example.posttest.etc.filter;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.Collection;

@Slf4j
public class CookieFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("-----------필터작동----------");
    }

    @Override
    public void destroy() {
        log.info("-----------필터작동종료----------");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse resp=(HttpServletResponse) servletResponse;
        log.info("가능?:{}",resp.isCommitted());
        System.out.println(resp);
        CustomRespWrapper customRespWrapper=new CustomRespWrapper(resp);

        filterChain.doFilter(servletRequest,customRespWrapper);

    }

}
