package com.example.posttest.service;


import com.example.posttest.Exceptions.*;
import com.example.posttest.dtos.MemberDto;
import com.example.posttest.dtos.UserSession;
import com.example.posttest.dtos.UserSessionTot;
import com.example.posttest.entitiy.Content;
import com.example.posttest.entitiy.Member;
import com.example.posttest.entitiy.UserAdmins;
import com.example.posttest.etc.*;
import com.example.posttest.repository.UserAdminRepo;
import com.example.posttest.repository.contentrepositories.ContentRepository;
import com.example.posttest.repository.memrepo.MemberRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service

@RequiredArgsConstructor
@Slf4j
public class MemberService {


    private final MemberRepository memberRepository;


    private final CookieRedisSession cookieRedisSession;


    private final RedisTemplate<String,String> redisTemplate;


    private final UserAdminRepo userAdminRepo;


    public ResponseEntity<ApiResponse<String>> memberassign(MemberDto memberDto,HttpServletRequest req){

        log.info("IP:{}",req.getHeader("X-Forwarded-For"));

        try{


            String salt=BCrypt.gensalt(10);
            String password=BCrypt.hashpw(memberDto.getPassword(),salt);
            Member member=new Member();
            UserAdmins userAdmins;
            if(memberDto.getEmail().equals("dong.3058@daum.net")) {
                userAdmins=new UserAdmins(UserAdmin.Admin);

                member = new Member(memberDto.getEmail(),password,userAdmins);

            }
            else{
                userAdmins=new UserAdmins(UserAdmin.User);
                member=new Member(memberDto.getEmail(),password,userAdmins);

            }

            member.setCreate_Time(LocalDateTime.now());
            userAdminRepo.save(userAdmins);
            memberRepository.save(member);
        return ResponseEntity.ok(ApiResponse.success("성공",ErrorMsgandCode.Successlogin.getMsg()));
        }
        catch(Exception e){


            throw new EtcError();
        }

    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<String>> memberexistcheck(MemberDto memberDto){

        Optional<Member> memberopt= memberRepository.findmember_beforeassign(memberDto.getEmail());
        if(memberopt.isPresent()){
            throw new ExistIdError();

        }


        return ResponseEntity.ok(ApiResponse.success("성공",ErrorMsgandCode.Successlogin.getMsg()));
    }







    public ResponseEntity<ApiResponse<MemberDto>> logincheck(HttpServletRequest req/*,String newtoken*/){


        UserSessionTot userSessionTot=cookieRedisSession.getusersessiontot(req);

        String session_id=userSessionTot.getSession_id();

        UserSession userSession=userSessionTot.getUserSession();


        Long member_id=(Long) userSession.getMember_id();

        Optional<Member> member=memberRepository.findById(member_id);

        if(member.isPresent()) {
            HttpHeaders httpHeaders = new HttpHeaders();


            httpHeaders.set("Csrf_check", userSession.getCrsf());

            return new ResponseEntity<>(ApiResponse.success(new MemberDto(member.get().getEmail(), ""), ErrorMsgandCode.Successlogin.getMsg()), httpHeaders, HttpStatus.OK);
        }

        throw new UnableToFindAccount();

    }



    @Transactional
    public ResponseEntity<ApiResponse<String>> changepassword(MemberDto memberDto){


            String salt = BCrypt.gensalt();
            String password = BCrypt.hashpw(memberDto.getPassword(), salt);
            int nums = memberRepository.changepassword(memberDto.getEmail(), password);

            return ResponseEntity.ok(ApiResponse.success("성공", ErrorMsgandCode.Successfind.getMsg()));


        //return ResponseEntity.ok(ApiResponse.success("업뎃실패",ErrorMsgandCode.Fail_Csrf_Auth.getMsg()));


    }

    @Transactional(readOnly = true)
    public String [] memberlogin(MemberDto memberDto) {

        Optional<Member> member = memberRepository.findmember_beforeassign(memberDto.getEmail());



        HashOperations<String,String,String> opsforhash=redisTemplate.opsForHash();
        String nums=(String) opsforhash.get("try_login",memberDto.getEmail());

        if(member.isEmpty()) {


            throw new UnableToFindAccount();
        }


        if(nums==null){
            opsforhash.put("try_login",memberDto.getEmail(),String.valueOf(1L));
        }
        else {

            if(Long.parseLong(nums)>=5){

                throw new AccessExceedError("에러발생",memberDto.getEmail());
            }

            opsforhash.put("try_login", memberDto.getEmail(), String.valueOf(Long.parseLong(nums) + 1L));

        }




        if(BCrypt.checkpw(memberDto.getPassword(), member.get().getPassword())){


         opsforhash.delete("try_login",memberDto.getEmail());

        String [] s=cookieRedisSession.makeyusersession(member.get().getMember_id());


        return  s;




        }





        throw new UnableToFindAccount();



    }

    @Transactional(readOnly = true)
    public String [] membersecurelogin(MemberDto memberDto) {

        Optional<Member> member = memberRepository.findmember_beforeassign(memberDto.getEmail());
        log.info("email:{} {}",memberDto.getEmail(),memberDto.getPassword());


        ValueOperations<String,String> op=redisTemplate.opsForValue();


        if(op.get(memberDto.getEmail()).equals(memberDto.getPassword())){

            redisTemplate.delete(memberDto.getEmail());
            redisTemplate.opsForHash().delete("try_login",memberDto.getEmail());

            String [] s=cookieRedisSession.makeyusersession(member.get().getMember_id());



            return  s;

        }



        throw new EtcError();



    }






    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest req){

        UserSessionTot userSessionTot=cookieRedisSession.getusersessiontot(req);

        HttpHeaders headers=cookieRedisSession.makecookieinheader(userSessionTot,"delete");

        return new ResponseEntity<>(ApiResponse.success("success",ErrorMsgandCode.Successlogin.getMsg()),headers,HttpStatus.OK);



    }



    private Optional<String> get_token_from_req(HttpServletRequest req){

        Cookie[] cookies=req.getCookies();
        if(cookies!=null){




            Optional<Cookie> cookie= Arrays.stream(cookies).filter(x->"back_access_token".equals(x.getName()))
                    .findFirst();
            if(cookie.isEmpty()){
                return Optional.empty();
            }


            Optional<String> access_token=Optional.ofNullable(cookie.get().getValue());


            return access_token;}
        return Optional.empty();
    }


}
