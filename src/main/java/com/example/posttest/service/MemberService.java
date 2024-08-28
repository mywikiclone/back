package com.example.posttest.service;


import com.example.posttest.Exceptions.ExistIdError;
import com.example.posttest.Exceptions.UnableToFindAccount;
import com.example.posttest.dtos.MemberDto;
import com.example.posttest.entitiy.Member;
import com.example.posttest.etc.ApiResponse;
import com.example.posttest.etc.ErrorMsgandCode;
import com.example.posttest.etc.JwtToken;
import com.example.posttest.etc.JwtUtil;
import com.example.posttest.repository.memrepo.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {


    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    private final RedisTemplate<String,String> redisTemplate;

    public void memberassign(MemberDto memberDto){
        Member member=new Member(memberDto.getEmail(),memberDto.getPassword());
        Optional<Member> memberopt= memberRepository.findmember_beforeassign(memberDto.getEmail());
        if(memberopt.isPresent()){
            throw new ExistIdError();

        }

        memberRepository.save(member);

    }

    public String memberlogin(MemberDto memberDto) {

        Optional<Member> member = memberRepository.findmember(memberDto.getEmail(), memberDto.getPassword());
        if (member.isEmpty()) {
            throw new UnableToFindAccount();
        }
        JwtToken jwtoken = jwtUtil.genjwt(member.get().getMember_id());

        //초창기 로그인시 발행한 access토큰과 리프래시 토큰을 짝지어서 저장. ttl은 5분 설정
        ValueOperations<String,String> valueOperations =redisTemplate.opsForValue();

        valueOperations.set(jwtoken.getAccesstoken(), jwtoken.getRefreshtoken(),180,TimeUnit.SECONDS);


        return jwtoken.getAccesstoken();
        //return ApiResponse.success(jwtoken.getAccesstoken(), ErrorMsgandCode.Successfind.getMsg());

    }

    public ResponseEntity<ApiResponse<String>> logout(String token){

        redisTemplate.delete(token);


        return new ResponseEntity(ApiResponse.success("로그아웃성공",null), HttpStatus.OK);




    }


}
