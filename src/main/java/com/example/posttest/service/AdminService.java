package com.example.posttest.service;


import com.example.posttest.Exceptions.AdminError;
import com.example.posttest.Exceptions.CantFindError;
import com.example.posttest.Exceptions.EtcError;
import com.example.posttest.dtos.AdminContentDto;
import com.example.posttest.dtos.AdminMemberDto;
import com.example.posttest.dtos.ContentDto;
import com.example.posttest.dtos.MemberDto;
import com.example.posttest.entitiy.Content;
import com.example.posttest.entitiy.Member;
import com.example.posttest.etc.ApiResponse;
import com.example.posttest.etc.ErrorMsgandCode;
import com.example.posttest.etc.LoginSessionConst;
import com.example.posttest.etc.UserAdmin;
import com.example.posttest.repository.contentrepositories.ContentRepository;
import com.example.posttest.repository.memrepo.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.eclipse.angus.mail.imap.AppendUID;
import org.hibernate.engine.spi.Resolution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminService {


    private final MemberRepository memberRepository;

    private final ContentService contentService;

    private final ContentRepository contentRepository;



    public ResponseEntity<ApiResponse<ContentDto>> findcontent(Long id,String title) {


        Member adminmember=checkuseradmin(id);


        Optional<Content> content = contentRepository.findbytitle(title);
        if (content.isEmpty()) {


            throw new CantFindError();
        }


        return ResponseEntity.ok(ApiResponse.success(new ContentDto(content.get().getContent_id(),content.get().getTitle(),content.get().getMember().getEmail(),content.get().getUpdate_Time(),content.get().getCreate_Time(),
                content.get().getGrade().name()),ErrorMsgandCode.Successfind.getMsg()));




    }

    public ResponseEntity<ApiResponse<String>> checkadmin(Long id){



        Member adminmember=checkuseradmin(id);


        return ResponseEntity.ok(ApiResponse.success("ok",ErrorMsgandCode.Successfind.getMsg()));


    }



    public ResponseEntity<ApiResponse<String>> changeuseradmin(Long id,AdminMemberDto adminMemberDto){

        Member adminmember=checkuseradmin(id);
        log.info("??:{}",adminMemberDto.getEmail());
        Optional<Member> member=memberRepository.findmember_beforeassign(adminMemberDto.getEmail());

        if(member.isPresent()){


            UserAdmin usergrade=supplyadmin(adminMemberDto.getGrade());

            int number=memberRepository.changeadmin(usergrade,adminMemberDto.getEmail());



            return ResponseEntity.ok(ApiResponse.success(usergrade.name(), ErrorMsgandCode.Successsave.getMsg()));

        }

        throw new CantFindError();



    }


    public ResponseEntity<ApiResponse<String>> changecontentadmin(Long id,AdminContentDto adminContentDto){


        Member adminmember=checkuseradmin(id);

        Optional<Content> content=contentRepository.findById(adminContentDto.getContent_id());


        if(content.isPresent()){

            UserAdmin userAdmin=supplyadmin(adminContentDto.getGrade());

            int number=contentRepository.changeadmin(userAdmin,adminContentDto.getContent_id());


            return ResponseEntity.ok(ApiResponse.success("성공", ErrorMsgandCode.Successsave.getMsg()));

        }


        throw new CantFindError();


    }

    public ResponseEntity<ApiResponse<List<ContentDto>>> getcontentlist(Long id){
        Member adminmember=checkuseradmin(id);
        Pageable pageable= PageRequest.of(0,12);
        Page<Content> contents=contentRepository.content_list(pageable);
        if(contents.isEmpty()){

            throw new CantFindError();
            //return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));
        }
        List<ContentDto> ContentDtoList=contents.stream()
                .map(content->{

                    return  new ContentDto(content.getContent_id(),content.getTitle(),content.getContent(),content.getMember().getEmail(),content.getUpdate_Time(),content.getGrade().name());
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(ContentDtoList,ErrorMsgandCode.Successfind.getMsg()));

    }

    public ResponseEntity<ApiResponse<AdminMemberDto>> findmeber(Long id,MemberDto memberDto){

        Member adminmember=checkuseradmin(id);
       Optional<Member> member= memberRepository.findmember_beforeassign(memberDto.getEmail());

       if(member.isEmpty()){


           throw new CantFindError();
       }

       return ResponseEntity.ok(ApiResponse.success(new AdminMemberDto(member.get().getEmail(),member.get().getGrade().name(),member.get().getCreate_Time()),ErrorMsgandCode.Successfind.getMsg()));


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
        log.info("member:{}  {}",member.isEmpty(),member.get().getGrade().name());

        if(!member.get().getGrade().name().equals("Admin")){
            throw new AdminError();
        }

        return member.get();

    }
}
