package com.example.posttest.controllers;


import com.example.posttest.dtos.MemberDto;
import com.example.posttest.etc.ApiResponse;
import com.example.posttest.etc.ErrorMsgandCode;
import com.example.posttest.etc.JwtToken;
import com.example.posttest.etc.annotataion.CheckNewToken;
import com.example.posttest.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberControllers {



    private final MemberService memberService;

    @Value("${spring.jwt.expiration}")
    private  Long expiration;

    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<String>> assign(@RequestBody MemberDto memberDTO){
        return memberService.memberassign(memberDTO);



        //return ResponseEntity.ok(ApiResponse.success("성공",ErrorMsgandCode.Successlogin.getMsg()));
    };


    @PostMapping("/idcheck")
    public ResponseEntity<ApiResponse<String>> check(@RequestBody MemberDto memberDto){


        return memberService.memberexistcheck(memberDto);


    }


    @PostMapping("/firlogin")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody MemberDto memberDTO,HttpServletRequest req){
        ApiResponse<String> x= memberService.memberlogin(memberDTO,req);
        ResponseCookie responseCookie=ResponseCookie.from("back_access_token","hello")
                .maxAge(expiration/1000)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain("localhost")
                .build();



        ResponseCookie responseCookie2=ResponseCookie.from("back_refresh_token","hello")
                .maxAge(expiration/1000)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain("localhost")
                .build();

        HttpHeaders headers=new HttpHeaders();

        headers.add(HttpHeaders.SET_COOKIE,responseCookie.toString());

        headers.add(HttpHeaders.SET_COOKIE,responseCookie2.toString());

        return new ResponseEntity<>(x,headers,HttpStatus.OK);
    };

    @PostMapping("/checkloginstate")
    public ResponseEntity<ApiResponse<MemberDto>> tokenlogin(HttpServletRequest req/*,@CheckNewToken String newtoken*/){

        return memberService.logincheck(req);
    }


    //레디스에 access토큰 저장, refresh 토크ㅜㄴ 저장하고 로그아웃 로직이랑
    //토큰 재생성을 위한 refresh토큰 이용,refresh토큰 재생성 이건 내일.


    @GetMapping("/logout")
    public ResponseEntity<ApiResponse<String>> Logout(HttpServletRequest req){

        HttpSession session=req.getSession(false);

        if(session!=null){


            session.invalidate();
        }



        return ResponseEntity.ok(ApiResponse.success("success",ErrorMsgandCode.Successlogin.getMsg()));



        /*Cookie[] cookies=req.getCookies();
        Optional<Cookie> cookie=Arrays.stream(cookies).filter(x->"back_refresh_token".equals(x.getName())).findFirst();
        ApiResponse<String> a=memberService.logout(cookie.get().getValue());
        ResponseCookie responseCookie=ResponseCookie.from("back_access_token",null)
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain("localhost")
                .build();

        ResponseCookie responseCookie2=ResponseCookie.from("back_refresh_token",null)
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain("localhost")
                .build();



        HttpHeaders headers=new HttpHeaders();


        headers.add(HttpHeaders.SET_COOKIE,responseCookie.toString());

        headers.add(HttpHeaders.SET_COOKIE,responseCookie2.toString());

        headers.add(HttpHeaders.SET_COOKIE,responseCookie.toString());


        return new ResponseEntity<>(a,headers,HttpStatus.OK);*/

      }

    @GetMapping("/healthycheck")
    public ResponseEntity<String> healthycheck(){

        return  ResponseEntity.ok("success");
    }



    public <T>ResponseEntity<ApiResponse<T>> return_ans_method(ApiResponse<T> apiResponse,String token){
        if(token==null){

            return ResponseEntity.ok(apiResponse);
        }

        ResponseCookie responseCookie=ResponseCookie.from("back_access_token",token)
                .maxAge(expiration/1000)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain("localhost")
                .build();
        HttpHeaders headers=new HttpHeaders();

        headers.add(HttpHeaders.SET_COOKIE,responseCookie.toString());

        return new ResponseEntity<>(apiResponse,headers, HttpStatus.OK);
    }



}
