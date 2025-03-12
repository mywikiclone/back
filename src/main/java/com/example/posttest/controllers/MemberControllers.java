package com.example.posttest.controllers;


import com.example.posttest.dtos.MemberDto;
import com.example.posttest.dtos.MsgDto;
import com.example.posttest.dtos.Oauth2Dto;
import com.example.posttest.dtos.UserSessionTot;
import com.example.posttest.etc.*;
import com.example.posttest.etc.annotataion.LoginUser;
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

    private final WebSocketController webSocketController;

    private final RedisSubPub redisSubPub;


    @Value("${spring.jwt.expiration}")
    private  Long expiration;

    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<String>> assign(@RequestBody MemberDto memberDTO,HttpServletRequest req){

        return memberService.memberassign(memberDTO,req);

    };

    @PostMapping("/oauth2/login")
    public ResponseEntity<ApiResponse<String>> oauth2login(@RequestBody Oauth2Dto oauth2Dto){

        String [] strs=memberService.oauth2login(oauth2Dto);




        ResponseCookie responseCookie=ResponseCookie.from("JSESSIONID2",strs[0])
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
        log.info("-----end------");

        return new ResponseEntity<>(x,headers,HttpStatus.OK);


    }




    @PostMapping("/idcheck")
    public ResponseEntity<ApiResponse<String>> check(@RequestBody MemberDto memberDto){


        return memberService.memberexistcheck(memberDto);



    }


    @PostMapping("/idcheckoauth2")
    public ResponseEntity<ApiResponse<String>> check2(@RequestBody MemberDto memberDto){


        return memberService.memberexistcheck2(memberDto);



    }




    @GetMapping("/logintest")
    public ResponseEntity<ApiResponse<String>> testinglogin(){

        webSocketController.SendingAnotherEnvLoginMsg("dong.3058@daum.net");
        return ResponseEntity.ok(ApiResponse.success("성공",ErrorMsgandCode.Successlogin.getMsg()));

    }
    @PostMapping("/firlogin")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody MemberDto memberDTO,HttpServletRequest req){


        String [] strs= memberService.memberlogin(memberDTO,req.getHeader("X-Forwarded-For"));


        ResponseCookie responseCookie=ResponseCookie.from("JSESSIONID2",strs[0])
                .secure(true)
                .httpOnly(true)
                .sameSite("strict")
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





    @PostMapping("/changepassword")
    public ResponseEntity<ApiResponse<String>> changepassword(@LoginUser UserSessionTot userSessionTot,MemberDto memberDto){
           return memberService.changepassword(userSessionTot,memberDto);

    }



    @PostMapping("/securelogin")
    public ResponseEntity<ApiResponse<String>> securelogin(@RequestBody MemberDto memberDTO,HttpServletRequest req){

        String [] strs= memberService.membersecurelogin(memberDTO,req.getHeader("X-Forwarded-For"));

        ResponseCookie responseCookie=ResponseCookie.from("JSESSIONID",strs[0])
                .secure(true)
                .httpOnly(true)
                .sameSite("strict")
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

    @GetMapping("/logouttesting")
    public void Logouttesting(HttpServletRequest req){

        memberService.logouttesting(req);




    }



}
