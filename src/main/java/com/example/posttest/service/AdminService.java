package com.example.posttest.service;


import com.example.posttest.Exceptions.AdminError;
import com.example.posttest.Exceptions.CantFindError;
import com.example.posttest.dtos.*;
import com.example.posttest.entitiy.Content;
import com.example.posttest.entitiy.Member;
import com.example.posttest.etc.*;
import com.example.posttest.repository.contentrepositories.ContentRepository;
import com.example.posttest.repository.memrepo.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {


    private final MemberRepository memberRepository;



    private final ContentRepository contentRepository;

    private final CookieRedisSession cookieRedisSession;


    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<ContentDto>> findcontent(UserSessionTot userSessionTot, String title) {


        Long id=userSessionTot.getUserSession().getMember_id();

        Member adminmember=checkuseradmin(id);



        Optional<Content> content = contentRepository.findbytitle(title);
        if (content.isEmpty()) {


            throw new CantFindError();
        }


        HttpHeaders headers=cookieRedisSession.makecookieinheader(userSessionTot,"extend");


        return new ResponseEntity(ApiResponse.success(new ContentDto(content.get().getContent_id(),content.get().getTitle(),content.get().getMember().getEmail(),content.get().getUpdate_Time(),content.get().getCreate_Time(),
                content.get().getGrade().name()),ErrorMsgandCode.Successfind.getMsg()),headers, HttpStatus.OK);




    }


    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<String>> checkadmin(UserSessionTot userSessionTot){

        Long id=userSessionTot.getUserSession().getMember_id();

        Member adminmember=checkuseradmin(id);


        return ResponseEntity.ok(ApiResponse.success("ok",ErrorMsgandCode.Successfind.getMsg()));


    }



    @Transactional
    public ResponseEntity<ApiResponse<String>> changeuseradmin(UserSessionTot userSessionTot,AdminMemberDto adminMemberDto){

        Long id=userSessionTot.getUserSession().getMember_id();

        Member adminmember=checkuseradmin(id);

        Optional<Member> member=memberRepository.findmember_beforeassign(adminMemberDto.getEmail());

        if(member.isPresent()){


            UserAdmin usergrade=supplyadmin(adminMemberDto.getGrade());

            member.get().setGrade(usergrade);

            HttpHeaders headers=cookieRedisSession.makecookieinheader(userSessionTot,"extend");

            return new ResponseEntity(ApiResponse.success(usergrade.name(), ErrorMsgandCode.Successsave.getMsg()),headers,HttpStatus.OK);

        }

        throw new CantFindError();



    }


    @Transactional
    //현재content update하는 서비스에서 비관적 락을 걸므로 이거랑 그거랑 동시에걸리면 큰일난단말이죠?
    //content의 유저권한을 따로분리해줘야될거같은대 admin의 경우 권한을 수정하는거니까 그냥 권한테이블을 따로분리해놓고
    //업데이트를 진행해줘야될듯???????
    public ResponseEntity<ApiResponse<String>> changecontentadmin(UserSessionTot userSessionTot,AdminContentDto adminContentDto){


        Long id=userSessionTot.getUserSession().getMember_id();
        Member adminmember=checkuseradmin(id);

        Optional<Content> content=contentRepository.findById(adminContentDto.getContent_id());


        if(content.isPresent()){

            UserAdmin userAdmin=supplyadmin(adminContentDto.getGrade());
            content.get().setGrade(userAdmin);

            HttpHeaders headers=cookieRedisSession.makecookieinheader(userSessionTot,"extend");

            return new ResponseEntity(ApiResponse.success("성공", ErrorMsgandCode.Successsave.getMsg()),headers,HttpStatus.OK);

        }


        throw new CantFindError();


    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<ContentDto>>> getcontentlist(UserSessionTot userSessionTot){

        Long id=userSessionTot.getUserSession().getMember_id();

        Member adminmember=checkuseradmin(id);
        Pageable pageable= PageRequest.of(0,12);
        Page<Content> contents=contentRepository.content_list(pageable);
        if(contents.isEmpty()){

            throw new CantFindError();
            //return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));
        }
        List<ContentDto> ContentDtoList=contents.stream()
                .map(content->{

                    return  new ContentDto(content.getContent_id(),content.getTitle(),content.getLobContent().getContent(),content.getMember().getEmail(),content.getUpdate_Time(),content.getGrade().name());
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(ContentDtoList,ErrorMsgandCode.Successfind.getMsg()));

    }


    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<AdminMemberDto>> findmeber(UserSessionTot userSessionTot,MemberDto memberDto){


        Long id=userSessionTot.getUserSession().getMember_id();
        Member adminmember=checkuseradmin(id);
       Optional<Member> member= memberRepository.findmember_beforeassign(memberDto.getEmail());

       if(member.isEmpty()){


           throw new CantFindError();
       }



       HttpHeaders headers=cookieRedisSession.makecookieinheader(userSessionTot,"extend");
       return new ResponseEntity(ApiResponse.success(new AdminMemberDto(member.get().getEmail(),member.get().getGrade().name(),member.get().getCreate_Time()),ErrorMsgandCode.Successfind.getMsg()),headers,HttpStatus.OK);


    }






    public UserAdmin supplyadmin(String gradename){

        UserAdmin usergrade;
        switch (gradename){

            case "Admin":

                usergrade=UserAdmin.Admin;
                break;
            case "Ban":

                usergrade=UserAdmin.Ban;
                break;

            default:

                usergrade=UserAdmin.User;


        }

        return usergrade;



    }

    public Member checkuseradmin(Long id){


        Optional<Member> member=memberRepository.findById(id);


        if(!member.get().getGrade().name().equals("Admin")){
            throw new AdminError();
        }

        return member.get();

    }
}
