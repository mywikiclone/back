package com.example.posttest.controllers;


import com.example.posttest.dtos.MemberDto;
import com.example.posttest.etc.ApiResponse;
import com.example.posttest.etc.ErrorMsgandCode;
import com.example.posttest.etc.JwtToken;
import com.example.posttest.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @PostMapping("/assign")
    public void assign(@RequestBody MemberDto memberDTO){
        memberService.memberassign(memberDTO);
    };


    @PostMapping("/firlogin")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody MemberDto memberDTO){
        String token=memberService.memberlogin(memberDTO);
        ResponseCookie responseCookie=ResponseCookie.from("back_access_token",token)
                .maxAge(120)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain("localhost")
                .build();

        HttpHeaders headers=new HttpHeaders();

        headers.add(HttpHeaders.SET_COOKIE,responseCookie.toString());

        return new ResponseEntity<>(ApiResponse.success(token, ErrorMsgandCode.Successfind.getMsg()),headers,HttpStatus.OK);
    };

    @GetMapping("/tokenlogin")
    public ResponseEntity<ApiResponse<String>> tokenlogin(){

        return ResponseEntity.ok(ApiResponse.success("로그인 성공", ErrorMsgandCode.Successlogin.getMsg()));
    }


    //레디스에 access토큰 저장, refresh 토크ㅜㄴ 저장하고 로그아웃 로직이랑
    //토큰 재생성을 위한 refresh토큰 이용,refresh토큰 재생성 이건 내일.


    @GetMapping("/logout")
    public ResponseEntity<ApiResponse<String>> Logout(HttpServletRequest req){
        String token=req.getHeader("Authorization").substring(7);
        return memberService.logout(token);
      }

    @GetMapping("/healthycheck")
    public String healthycheck(){

        return "ok";
    }


}
