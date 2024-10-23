package com.example.posttest.etc.filter;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

@Slf4j

public class CustomRespWrapper extends HttpServletResponseWrapper {
    public CustomRespWrapper(HttpServletResponse resp) {
        super(resp);
    }

    @Override
    public void addCookie(Cookie cookie) {
        log.info("name:{}",cookie.getName());
        // JSESSIONID 쿠키를 감지하여 수정
        if ("JSESSIONID".equals(cookie.getName())) {
            // 쿠키 속성 수정
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/"); // 필요한 경로 설정
            // 필요에 따라 SameSite 속성 추가 등
        }
        super.addCookie(cookie); // 원래의 메서드 호출
    }


}