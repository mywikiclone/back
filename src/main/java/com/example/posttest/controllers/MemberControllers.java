package com.example.posttest.controllers;


import com.example.posttest.dtos.MemberDto;
import com.example.posttest.etc.*;
import com.example.posttest.service.MemberService;


import jakarta.servlet.http.HttpServletRequest;
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

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberControllers {



    private final MemberService memberService;

    private final JwtUtil jwtUtil;

    @Value("${spring.jwt.expiration}")
    private  Long expiration;

    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<String>> assign(@RequestBody MemberDto memberDTO,HttpServletRequest req){
        return memberService.memberassign(memberDTO,req);



        //return ResponseEntity.ok(ApiResponse.success("성공",ErrorMsgandCode.Successlogin.getMsg()));
    };





    @PostMapping("/idcheck")
    public ResponseEntity<ApiResponse<String>> check(@RequestBody MemberDto memberDto){


        return memberService.memberexistcheck(memberDto);


    }


    @PostMapping("/firlogin")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody MemberDto memberDTO,HttpServletRequest req){
        log.info("request header:{}",req.getRemoteUser());
        log.info("request ip:{}",req.getRemoteAddr());
        log.info("request object:{}",req);
        log.info("request header2:{}",req.getHeader("X-Forwarded-For"));
        String [] strs= memberService.memberlogin(memberDTO);

        ResponseCookie responseCookie=ResponseCookie.from("JSESSIONID",strs[0])
                .secure(true)
                .httpOnly(true)
                .sameSite("none")
                .maxAge(1800)
                .domain("localhost")
                .path("/")
                .build();


        HttpHeaders headers=new HttpHeaders();
        headers.add("Csrf_check",strs[1]);
        headers.add(HttpHeaders.SET_COOKIE,responseCookie.toString());
        ApiResponse x= ApiResponse.success("success",ErrorMsgandCode.Successlogin.getMsg());

        return new ResponseEntity<>(x,headers,HttpStatus.OK);
    };

    @PostMapping("/checkloginstate")
    public ResponseEntity<ApiResponse<MemberDto>> tokenlogin(HttpServletRequest req/*,@CheckNewToken String newtoken*/){

        return memberService.logincheck(req);
    }


    //레디스에 access토큰 저장, refresh 토크ㅜㄴ 저장하고 로그아웃 로직이랑
    //토큰 재생성을 위한 refresh토큰 이용,refresh토큰 재생성 이건 내일.


    @PostMapping("/changepassword")
    public ResponseEntity<ApiResponse<String>> changepassword(@RequestBody MemberDto memberDTO){

           return memberService.changepassword(memberDTO);

    }



    @PostMapping("/securelogin")
    public ResponseEntity<ApiResponse<String>> securelogin(@RequestBody MemberDto memberDTO){
        String [] strs= memberService.membersecurelogin(memberDTO);

        ResponseCookie responseCookie=ResponseCookie.from("JSESSIONID",strs[0])
                .secure(true)
                .httpOnly(true)
                .sameSite("none")
                .maxAge(1800)
                .domain("localhost")
                .path("/")
                .build();


        HttpHeaders headers=new HttpHeaders();
        headers.add("Csrf_check",strs[1]);
        headers.add(HttpHeaders.SET_COOKIE,responseCookie.toString());
        ApiResponse x= ApiResponse.success("success",ErrorMsgandCode.Successlogin.getMsg());

        return new ResponseEntity<>(x,headers,HttpStatus.OK);
    };




    @GetMapping("/logout")
    public ResponseEntity<ApiResponse<String>> Logout(HttpServletRequest req){


        return memberService.logout(req);





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
