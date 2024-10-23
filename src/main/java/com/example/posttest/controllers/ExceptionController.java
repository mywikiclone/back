package com.example.posttest.controllers;


import com.example.posttest.Exceptions.ExistIdError;
import com.example.posttest.Exceptions.ReLoginError;
import com.example.posttest.Exceptions.UnableToFindAccount;
import com.example.posttest.etc.ApiResponse;
import com.example.posttest.etc.ErrorMsgandCode;
import com.example.posttest.etc.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.persistence.ElementCollection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ExceptionController {

    private final JwtUtil jwtUtil;

    @ExceptionHandler({MalformedJwtException.class,UnsupportedJwtException.class})
    public ResponseEntity<ApiResponse<String>> jwterrors(Exception ex){
        log.info("에러종류:{}",ex.getClass());

        return new ResponseEntity<>(ApiResponse.fail(ErrorMsgandCode.FailJwt.getMsg()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExistIdError.class)
    public ResponseEntity<ApiResponse<String>> idexist(Exception ex){
        log.info("에러종류:{}",ex.getClass());
        return new ResponseEntity<>(ApiResponse.fail(ErrorMsgandCode.FailIdExist.getMsg()),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnableToFindAccount.class)
    public ResponseEntity<ApiResponse<String>> UnableToFind(Exception ex){
        log.info("에러종류:{}",ex.getClass());
        return new ResponseEntity<>(ApiResponse.fail(ErrorMsgandCode.FailNoExistMember.getMsg()),HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ReLoginError.class)
    public ResponseEntity<ApiResponse<String>> reloginerror(Exception ex){
        log.info("에러종류:{}",ex.getClass());
        return new ResponseEntity<>(ApiResponse.fail(ErrorMsgandCode.Fail_Need_ReLogin.getMsg()),HttpStatus.BAD_REQUEST);
    }



}
