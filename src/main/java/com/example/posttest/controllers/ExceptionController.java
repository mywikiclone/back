package com.example.posttest.controllers;


import com.example.posttest.Exceptions.*;
import com.example.posttest.etc.ApiResponse;
import com.example.posttest.etc.ErrorMsgandCode;
import com.example.posttest.etc.JwtUtil;
import com.example.posttest.etc.logininterceptors.CustomWebSocketDecorator;
import com.example.posttest.etc.logininterceptors.WebSocketSecureInterCeptor;
import com.example.posttest.service.EmailService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.persistence.ElementCollection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ExceptionController {

    private final EmailService emailService;

    private final CustomWebSocketDecorator customWebSocketDecorator;



    @ExceptionHandler({CsrfError.class})
    public ResponseEntity<ApiResponse<String>> csrferrors(Exception ex){
        log.info("에러종류:{}",ex.getClass());

        return new ResponseEntity<>(ApiResponse.fail(ErrorMsgandCode.Fail_Csrf_Auth.getMsg()), HttpStatus.OK);
    }

    @ExceptionHandler(ExistIdError.class)
    public ResponseEntity<ApiResponse<String>> idexist(Exception ex){
        log.info("에러종류:{}",ex.getClass());
        return new ResponseEntity<>(ApiResponse.fail(ErrorMsgandCode.FailIdExist.getMsg()),HttpStatus.OK);
    }

    @ExceptionHandler(UnableToFindAccount.class)
    public ResponseEntity<ApiResponse<String>> UnableToFind(Exception ex){
        log.info("에러종류:{}",ex.getClass());
        return new ResponseEntity<>(ApiResponse.fail(ErrorMsgandCode.FailNoExistMember.getMsg()),HttpStatus.OK);
    }


    @ExceptionHandler(ReLoginError.class)
    public ResponseEntity<ApiResponse<String>> reloginerror(Exception ex){
        log.info("에러종류:{}",ex.getClass());
        return new ResponseEntity<>(ApiResponse.fail(ErrorMsgandCode.Fail_Need_ReLogin.getMsg()),HttpStatus.OK);
    }


    @ExceptionHandler({EtcError.class/*Exception.class*/})
    public ResponseEntity<ApiResponse<String>> EtcError(Exception ex){
        log.info("에러종류:{}",ex.getClass());
        log.info("에러내용:{}",ex.getMessage());
        return new ResponseEntity<>(ApiResponse.fail(ErrorMsgandCode.Fail_Etc_Error.getMsg()),HttpStatus.OK);
    }


    @ExceptionHandler(CantFindError.class)
    public ResponseEntity<ApiResponse<String>> CantFindError(Exception ex){
        log.info("에러종류:{}",ex.getClass());
        return new ResponseEntity<>(ApiResponse.fail(ErrorMsgandCode.Fail_Find.getMsg()),HttpStatus.OK);
    }


    @ExceptionHandler(AccessExceedError.class)
    public ResponseEntity<ApiResponse<String>> accessserror(AccessExceedError ex){
        log.info("에러종류:{}",ex.getClass());
        String email=ex.getEmail();
        emailService.create_auth_for_login(email);

        return new ResponseEntity<>(ApiResponse.fail(ErrorMsgandCode.Fail_Access_Excced_Error.getMsg()),HttpStatus.OK);

    }


    @ExceptionHandler(AdminError.class)
    public ResponseEntity<ApiResponse<String>> AdminError(Exception ex){
        log.info("에러종류:{}",ex.getClass());
        return new ResponseEntity<>(ApiResponse.fail(ErrorMsgandCode.Fail_No_Power.getMsg()),HttpStatus.OK);
    }


    /*@MessageExceptionHandler(WebSocketError.class)
    @SendToUser("/queue/errors")
    public String errormesg(Exception ex) throws IOException {



        log.info("에러메시지 내용까보기:{}",ex.getMessage());

        customWebSocketDecorator.closesession(ex.getMessage());

        return "사용자 에러발생";
    }*/








}
