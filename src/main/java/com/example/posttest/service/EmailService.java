package com.example.posttest.service;


import com.example.posttest.Exceptions.CantFindError;
import com.example.posttest.Exceptions.EtcError;
import com.example.posttest.dtos.MailAuthDto;
import com.example.posttest.dtos.MailAuthDto2;
import com.example.posttest.dtos.MemberDto;
import com.example.posttest.etc.ApiResponse;
import com.example.posttest.etc.ErrorMsgandCode;
import com.example.posttest.repository.memrepo.MemberRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import static org.springframework.http.ResponseEntity.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;


    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private Integer time;
    private final RedisTemplate<String,String> redisTemplate;
    
    
    private final MemberService memberService;

    public ResponseEntity<ApiResponse<String>> create_password(String email){
        log.info("mail service:{} {}",senderEmail,time);
        int number=(int)(Math.random()*(999))+100;

        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        try {

     
            log.info("보낼주소:{}",email);
            mimeMessage.setFrom(senderEmail);
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO,email);
            mimeMessage.setText(Integer.toString(number),"UTF-8","plain");
            javaMailSender.send(mimeMessage);
            
            
           ResponseEntity<ApiResponse<String>> resp=memberService.changepassword(new MemberDto(email,String.valueOf(number)));

           if(resp.getBody().getMsg().equals("업뎃실패")){
               throw new Exception();
           }
            
            
            return ok(ApiResponse.success("메일전송성공", ErrorMsgandCode.Successlogin.getMsg()));
        }
        catch(Exception e){

            throw new EtcError();
            //return ok(ApiResponse.fail("메일전송실패"));
        }


    }
    
    
    
    
    public ResponseEntity<ApiResponse<String>> create_auth(String email){
        log.info("mail service:{} {}",senderEmail,time);
        int number=(int)(Math.random()*(999))+100;
        ValueOperations<String,String> valueOperations =redisTemplate.opsForValue();

        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        try {

            if(valueOperations.get(email)!=null){

                redisTemplate.delete(email);


            }

            log.info("보낼주소:{}",email);
            mimeMessage.setFrom(senderEmail);
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO,email);
            mimeMessage.setText(Integer.toString(number),"UTF-8","plain");
            javaMailSender.send(mimeMessage);
            valueOperations.set(email,Integer.toString(number),time, TimeUnit.SECONDS);

            return ok(ApiResponse.success("메일전송성공", ErrorMsgandCode.Successlogin.getMsg()));
        }
        catch(Exception e){

            throw new EtcError();
            //return ok(ApiResponse.fail("메일전송성공"));
        }


    }


    public ResponseEntity<ApiResponse<String>> check_auth(MailAuthDto2 mailAuthDto2){


        if(redisTemplate.opsForValue().get(mailAuthDto2.getEmail())!=null){
            int code=Integer.parseInt(redisTemplate.opsForValue().get(mailAuthDto2.getEmail()));
            int code2=Integer.parseInt(mailAuthDto2.getCode());
            if(code==code2){

                redisTemplate.delete(mailAuthDto2.getEmail());
                return ResponseEntity.ok(ApiResponse.success("성공",ErrorMsgandCode.Successfind.getMsg()));


            }


        }

        throw new CantFindError();
        //return ResponseEntity.ok(ApiResponse.fail("실퍄ㅐ"));



    }

}
