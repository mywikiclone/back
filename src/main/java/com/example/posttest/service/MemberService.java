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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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

    public ApiResponse<String> memberlogin(MemberDto memberDto) {

        Optional<Member> member = memberRepository.findmember(memberDto.getEmail(), memberDto.getPassword());
        if (member.isEmpty()) {
            throw new UnableToFindAccount();
        }
        JwtToken jwtoken = jwtUtil.genjwt(member.get().getMember_id());

        //초창기 로그인시 발행한 access토큰과 리프래시 토큰을 짝지어서 저장. ttl은 5분 설정
        ValueOperations<String,String> valueOperations =redisTemplate.opsForValue();

        valueOperations.set(jwtoken.getAccesstoken(), jwtoken.getRefreshtoken(),3600L,TimeUnit.SECONDS);



        return ApiResponse.success(jwtoken.getAccesstoken(), ErrorMsgandCode.Successfind.getMsg());

    }


}
