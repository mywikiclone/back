package com.example.posttest.controllers;

import com.example.posttest.dtos.MailAuthDto;
import com.example.posttest.dtos.MailAuthDto2;
import com.example.posttest.etc.ApiResponse;
import com.example.posttest.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/requestmailauth")
    public ResponseEntity<ApiResponse<String>> mailauthrequest(@RequestBody  MailAuthDto mailAuthDto){

        return emailService.create_auth(mailAuthDto.getMail_code());


    }

    @PostMapping("/requestpassword")
    public ResponseEntity<ApiResponse<String>> password(@RequestBody MailAuthDto mailAuthDto){

       return  emailService.create_password(mailAuthDto.getMail_code());

    }


    @PostMapping("/mailauth")
    public ResponseEntity<ApiResponse<String>> mailauth(@RequestBody MailAuthDto2 mailAuthDto2){


        return emailService.check_auth(mailAuthDto2);


    }


}
