package com.example.posttest.service;


import com.example.posttest.Exceptions.ExistIdError;
import com.example.posttest.Exceptions.UnableToFindAccount;
import com.example.posttest.dtos.MemberDto;
import com.example.posttest.entitiy.Member;
import com.example.posttest.etc.*;
import com.example.posttest.repository.memrepo.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {


    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    private final RedisTemplate<String,String> redisTemplate;


    @Value("${spring.jwt.expiration}")
    private  Long expiration;


    public ResponseEntity<ApiResponse<String>> memberassign(MemberDto memberDto){
        String salt=BCrypt.gensalt();
        String password=BCrypt.hashpw(memberDto.getPassword(),salt);
        Member member=new Member(salt,memberDto.getEmail(),password);
        Optional<Member> memberopt= memberRepository.findmember_beforeassign(memberDto.getEmail());
        if(memberopt.isPresent()){
            throw new ExistIdError();

        }

        memberRepository.save(member);



        return ResponseEntity.ok(ApiResponse.success("성공",ErrorMsgandCode.Successlogin.getMsg()));

    }

    public ResponseEntity<ApiResponse<String>> memberexistcheck(MemberDto memberDto){

        Optional<Member> memberopt= memberRepository.findmember_beforeassign(memberDto.getEmail());
        if(memberopt.isPresent()){
            throw new ExistIdError();

        }


        return ResponseEntity.ok(ApiResponse.success("성공",ErrorMsgandCode.Successlogin.getMsg()));
    }









    public ResponseEntity<ApiResponse<MemberDto>> logincheck(HttpServletRequest req/*,String newtoken*/){


        HttpSession session=req.getSession(false);

        if(session==null){

            return ResponseEntity.ok(ApiResponse.fail("실패"));

        }

        Long member_id=(Long) session.getAttribute(LoginSessionConst.session_const);

        Optional<Member> member=memberRepository.findById(member_id);



        return ResponseEntity.ok(ApiResponse.success(new MemberDto(member.get().getEmail(),""),ErrorMsgandCode.Successlogin.getMsg()));
       /* Optional<String> token=get_token_from_req(req);


        if(token.isEmpty()){

            Long id= jwtUtil.getidfromtoken(newtoken);
            Optional<Member> member=memberRepository.findById(id);

            return ApiResponse.success(new MemberDto(member.get().getEmail(),""),ErrorMsgandCode.Successlogin.getMsg());
        }


        Long id= jwtUtil.getidfromtoken(token.get());
        Optional<Member> member=memberRepository.findById(id);


        return ApiResponse.success(new MemberDto(member.get().getEmail(),""),ErrorMsgandCode.Successlogin.getMsg());*/

    }



    public ApiResponse<String> memberlogin(MemberDto memberDto, HttpServletRequest req) {

        Optional<Member> member = memberRepository.findmember(memberDto.getEmail());
        if (member.isEmpty()) {
            throw new UnableToFindAccount();
        }

        String Hashed=BCrypt.hashpw(member.get().getPassword(),member.get().getSalt());

        if(BCrypt.checkpw(memberDto.getPassword(),Hashed)){



        HttpSession session=req.getSession();

        session.setAttribute(LoginSessionConst.session_const,member.get().getMember_id());



        //return ResponseEntity.ok(ApiResponse.success("success",ErrorMsgandCode.Successlogin.getMsg()));

        return ApiResponse.success("success",ErrorMsgandCode.Successlogin.getMsg());}



        throw new UnableToFindAccount();
        /*JwtToken jwtoken = jwtUtil.genjwt(member.get().getMember_id());
        //초창기 로그인시 발행한 access토큰과 리프래시 토큰을 짝지어서 저장. ttl은 5분 설정
        ValueOperations<String,String> valueOperations =redisTemplate.opsForValue();

        //valueOperations.set(jwtoken.getAccesstoken(), jwtoken.getRefreshtoken(),180,TimeUnit.SECONDS);

        valueOperations.set(jwtoken.getRefreshtoken(),member.get().getMember_id().toString(),expiration/1000,TimeUnit.SECONDS);

        List<String> tokens=new ArrayList<>();
        tokens.add(jwtoken.getAccesstoken());
        tokens.add(jwtoken.getRefreshtoken());
        return tokens;*/
        //return ApiResponse.success(jwtoken.getAccesstoken(), ErrorMsgandCode.Successfind.getMsg());

    }

    public ApiResponse<String> logout(String token){

        redisTemplate.delete(token);


        return ApiResponse.success("성공",ErrorMsgandCode.Successlogin.getMsg());



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
