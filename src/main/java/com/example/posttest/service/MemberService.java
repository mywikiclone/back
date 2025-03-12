package com.example.posttest.service;


import com.example.posttest.Exceptions.*;
import com.example.posttest.controllers.WebSocketController;
import com.example.posttest.dtos.*;
import com.example.posttest.entitiy.Member;
import com.example.posttest.etc.*;
import com.example.posttest.repository.memrepo.MemberRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
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


    private final WebSocketController webSocketController;


    private final RedisSubPub redisSubPub;


    public ResponseEntity<ApiResponse<String>> memberassign(MemberDto memberDto,HttpServletRequest req){


        try{

            String salt=BCrypt.gensalt(10);
            String password=BCrypt.hashpw(memberDto.getPassword(),salt);
            Member member=new Member();
            if(memberDto.getEmail().equals("dong.3058@daum.net")) {


                member = new Member(memberDto.getEmail(),password,UserAdmin.Admin);

            }
            else{

                member=new Member(memberDto.getEmail(),password,UserAdmin.User);

            }

            member.setCreate_Time(LocalDateTime.now());

            memberRepository.save(member);
        return ResponseEntity.ok(ApiResponse.success("성공",ErrorMsgandCode.Successlogin.getMsg()));
        }
        catch(Exception e){


            throw new EtcError();
        }

    }





    @Transactional
    public String [] oauth2login(Oauth2Dto oauth2Dto){

        String email=oauth2Dto.getEmail();

        Optional<Member> member=memberRepository.findmember_beforeassign(email);


        if(member.isEmpty()){

            Member member_s=new Member();

            if(email.equals("dong.3058@daum.net")) {


                member_s = new Member(email,UserAdmin.Admin,"true");
               member= Optional.of(memberRepository.save(member_s));
                String [] s=cookieRedisSession.makeyusersession(member.get().getMember_id());

                return s;
            }
            else{

                member_s=new Member(email,UserAdmin.User,"true");
                member= Optional.of(memberRepository.save(member_s));
                String [] s=cookieRedisSession.makeyusersession(member.get().getMember_id());

                return s;
            }



        }

        else{
            if(member.get().getOauth2_login().equals("true")){
                //webSocketController.SendingAnotherEnvLoginMsg(email);

                MsgDto msgDto=new MsgDto("logout","1",0L,"로그아웃 진행");
                redisSubPub.send_msg_to_msg_server(msgDto);
                String [] s=cookieRedisSession.makeyusersession(member.get().getMember_id());

                return s;
            }

            throw new UnableToFindAccount();
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

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<String>> memberexistcheck2(MemberDto memberDto){

        Optional<Member> memberopt= memberRepository.findmember_beforeassign(memberDto.getEmail());
        if(memberopt.isEmpty()||memberopt.get().getOauth2_login().equals("true")){
            log.info("없거나 oatuh로그인임");
            throw new ExistIdError();

        }


        return ResponseEntity.ok(ApiResponse.success("성공",ErrorMsgandCode.Successlogin.getMsg()));
    }








    public ResponseEntity<ApiResponse<MemberDto>> logincheck(HttpServletRequest req){


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
    public ResponseEntity<ApiResponse<String>> changepassword(UserSessionTot userSessionTot,MemberDto memberDto){

        Optional<Member> member = memberRepository.findById(userSessionTot.getUserSession().getMember_id());
        if(member.get().getOauth2_login().equals("true")){

            log.info("oauth2로그인 걸림");
            throw new UnableToFindAccount();

        }


        String salt = BCrypt.gensalt();
        String password = BCrypt.hashpw(memberDto.getPassword(), salt);
        int nums = memberRepository.changepassword(member.get().getEmail(), password);

        return ResponseEntity.ok(ApiResponse.success("성공", ErrorMsgandCode.Successfind.getMsg()));

    }



    @Transactional
    public ResponseEntity<ApiResponse<String>> changepassword(MemberDto memberDto){

        Optional<Member> member = memberRepository.findmember_beforeassign(memberDto.getEmail());
        if(member.get().getOauth2_login().equals("true")){

            log.info("oauth2로그인 걸림");
            throw new UnableToFindAccount();

        }


        String salt = BCrypt.gensalt();
        String password = BCrypt.hashpw(memberDto.getPassword(), salt);
        int nums = memberRepository.changepassword(member.get().getEmail(), password);

        return ResponseEntity.ok(ApiResponse.success("성공", ErrorMsgandCode.Successfind.getMsg()));

    }

    @Transactional
    public String [] memberlogin(MemberDto memberDto,String ip) {

        Optional<Member> member = memberRepository.findmember_beforeassign(memberDto.getEmail());



        HashOperations<String,String,String> opsforhash=redisTemplate.opsForHash();
        String nums=(String) opsforhash.get("try_login",memberDto.getEmail());


        if(member.get().getOauth2_login().equals("true")){
            log.info("oauth2로그인 걸림");
            throw new UnableToFindAccount();
        }

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

            webSocketController.SendingAnotherEnvLoginMsg(memberDto.getEmail());
            if(member.get().getAccess_ip()!=null) {
                if(!ip.equals(member.get().getAccess_ip())){


                    opsforhash.put("try_login",memberDto.getEmail(),String.valueOf(5L));
                    throw new AccessExceedError("에러발생",memberDto.getEmail());
                }
            }
            else{
                Member member2 = member.get();
                member2.setAccess_ip(ip);

                memberRepository.save(member2);

            }

            opsforhash.delete("try_login",memberDto.getEmail());
            String [] s=cookieRedisSession.makeyusersession(member.get().getMember_id());
            return  s;




        }



        throw new UnableToFindAccount();



    }

    @Transactional
    public String [] membersecurelogin(MemberDto memberDto,String ip) {

        Optional<Member> member = memberRepository.findmember_beforeassign(memberDto.getEmail());
        log.info("email:{} {}",memberDto.getEmail(),memberDto.getPassword());


        ValueOperations<String,String> op=redisTemplate.opsForValue();


        if(op.get(memberDto.getEmail()).equals(memberDto.getPassword())){

            redisTemplate.delete(memberDto.getEmail());
            redisTemplate.opsForHash().delete("try_login",memberDto.getEmail());
            Member member2 = member.get();
            member2.setAccess_ip(ip);
            memberRepository.save(member2);
            String [] s=cookieRedisSession.makeyusersession(member.get().getMember_id());



            return  s;

        }



        throw new EtcError();



    }






    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest req){

        UserSessionTot userSessionTot=cookieRedisSession.getusersessiontot(req);

        HttpHeaders headers=cookieRedisSession.makecookieinheader(userSessionTot,"delete");
        log.info("logout");


        redisSubPub.send_msg_to_msg_server(new MsgDto("delsse", "1", userSessionTot.getUserSession().getMember_id(), "test"));


        return new ResponseEntity<>(ApiResponse.success("success",ErrorMsgandCode.Successlogin.getMsg()),headers,HttpStatus.OK);



    }

    public void logouttesting(HttpServletRequest req){

        //UserSessionTot userSessionTot=cookieRedisSession.getusersessiontot(req);

        //HttpHeaders headers=cookieRedisSession.makecookieinheader(userSessionTot,"delete");
        //log.info("logout");


        redisSubPub.send_msg_to_msg_server(new MsgDto("delsse", "1", 0L, "test"));


        //return new ResponseEntity<>(ApiResponse.success("success",ErrorMsgandCode.Successlogin.getMsg()),headers,HttpStatus.OK);



    }






}
