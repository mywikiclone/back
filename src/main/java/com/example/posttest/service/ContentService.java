package com.example.posttest.service;

import com.example.posttest.Exceptions.UnableToFindAccount;
import com.example.posttest.dtos.*;
import com.example.posttest.entitiy.ChangeLog;
import com.example.posttest.entitiy.Content;
import com.example.posttest.entitiy.Member;
import com.example.posttest.entitiy.RealTimeIssue;
import com.example.posttest.etc.ApiResponse;
import com.example.posttest.etc.ErrorMsgandCode;
import com.example.posttest.repository.changelogrepo.ChangeLongRepo;
import com.example.posttest.repository.contentrepositories.ContentRepository;
import com.example.posttest.repository.memrepo.MemberRepository;
import com.example.posttest.repository.realtimerepo.RealTimeRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ContentService {


    private final ContentRepository contentRepository;
    private final ChangeLongRepo changeLongRepo;
    private final RealTimeRepo realTimeRepo;
    private final MemberRepository memberRepository;


    public ResponseEntity<ApiResponse<ContentDto>> ContentSave(ContentDto contentDto, Long member_id){


            Optional<Member> member=memberRepository.findById(member_id);

            Content content=new Content(member.get(),contentDto.getTitle(),contentDto.getContent());

            content.setCreate_Time(LocalDateTime.now());
            content.setUpdate_Time(LocalDateTime.now());

            Content content1=contentRepository.save(content);
            log.info("content1:{} {} {} {} {}",content1.getTitle(),content1.getContent_id(),content1.getUpdate_Time(),content.getContent(),content1.getMember().getEmail());

            return ResponseEntity.ok(ApiResponse.success(new ContentDto(content1.getContent_id(),content1.getTitle(),content1.getContent(),content1.getMember().getEmail(),content1.getUpdate_Time()),ErrorMsgandCode.Successfind.getMsg()));
        }


    public ResponseEntity<ApiResponse<String>> UpdateContent(Long member_id,ContentDto contentDto){
            Optional<Content> content_opt=contentRepository.findById(contentDto.getContent_id());




            LocalDateTime now=LocalDateTime.now();
            if(content_opt.isPresent()){
                int x=contentRepository.updatecontent(contentDto.getTitle(), contentDto.getContent(),now,contentDto.getContent_id());
                Optional<Member> member=memberRepository.findById(member_id);
                log.info("content_dto,member_id:{} {}",contentDto,member_id);
                log.info("object check:{} {}",content_opt.get(),member.get());
                ChangeLog changeLog=new ChangeLog(content_opt.get(),contentDto.getContent(),member.get());
                changeLog.setCreate_Time(now);
                changeLongRepo.save(changeLog);


            return ResponseEntity.ok(ApiResponse.success(now.toString(),ErrorMsgandCode.Successupdate.getMsg()));
            }

            return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));

    }



    /*
    *
    * 찾앗다는걸 기록하는 메서드.
    *
    * */

    public void saveRealTime(Content content){
        RealTimeIssue realTimeIssue=new RealTimeIssue();

        realTimeIssue.setContent(content);
        realTimeIssue.setSearched_time(LocalDateTime.now());
        realTimeRepo.save(realTimeIssue);
    }




    public ResponseEntity<ApiResponse<ContentDto>> FindContent(Long id){
        Optional<Content> content=contentRepository.findById(id);

        if(content.isEmpty()){


            return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));
        }
        saveRealTime(content.get());
        ContentDto contentDto=new ContentDto(content.get().getContent_id(),content.get().getTitle(),content.get().getContent(),content.get().getMember().getEmail(),content.get().getUpdate_Time());
        return ResponseEntity.ok(ApiResponse.success(contentDto,ErrorMsgandCode.Successfind.getMsg()));
    }


    public ResponseEntity<ApiResponse<ContentDto>> FindContent(){
        Optional<Content> content=contentRepository.random_logic();

        if(content.isPresent()) {
            saveRealTime(content.get());
            return ResponseEntity.ok(ApiResponse.success(new ContentDto(content.get().getContent_id(),content.get().getTitle(),content.get().getContent(),content.get().getMember().getEmail(),content.get().getUpdate_Time()), ErrorMsgandCode.Successfind.getMsg()));
        }


        return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));
        }

    public ApiResponse<ContentDto> FindContent(String title){
        Optional<Content> content=contentRepository.findbytitle(title);

        if(content.isEmpty()){


            return ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg());
        }
        saveRealTime(content.get());
        ContentDto contentDto=new ContentDto(content.get().getContent_id(),content.get().getTitle(),content.get().getContent(),content.get().getMember().getEmail(),content.get().getUpdate_Time());
        return ApiResponse.success(contentDto,ErrorMsgandCode.Successfind.getMsg());
    }





    public ResponseEntity<ApiResponse<List<ChangeLogDto>>> getchangelog(int page_num, Long id){
         Pageable page=PageRequest.of(page_num,12);
         Page<ChangeLogDto> changeLogs=changeLongRepo.getchangelogs(id,page);
         log.info("최대 페이징갯수:{}",changeLogs.getTotalPages());
         if(changeLogs.isEmpty()){

             return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));
         }
         /*List<ChangeLogDto> changeLogDtoList=changeLogs.stream()
                 .map(x->{
                   return  new ChangeLogDto(x.getLog_Id(),x.getCreate_Time());
                 })
                 .collect(Collectors.toList());*/
         return ResponseEntity.ok(ApiResponse.success(changeLogs.stream().toList(),ErrorMsgandCode.Successfind.getMsg()));
    }

    public ResponseEntity<ApiResponse<ContentDto>> getchanagelogtext(Long id){
        Optional<ChangeLog> changeLog=changeLongRepo.findById(id);



        if(changeLog.isPresent()){
        ChangeLog c=changeLog.get();
        ContentDto contentDto=new ContentDto(c.getMember().getMember_id(),c.getContent().getTitle(),c.getChanged_Content(),c.getUpdate_Time());


        return ResponseEntity.ok(ApiResponse.success(contentDto,ErrorMsgandCode.Successfind.getMsg()));}
        return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));
    }



    public void testing(){

    Optional<RealTimeIssue> rel=realTimeRepo.getreal();
    log.info("rel:{}",rel);
    }



    public ResponseEntity<ApiResponse<List<RealTimeIssueDto>>> getRealtimeissue(){

        Pageable pageable=PageRequest.of(0,10);
        LocalDateTime now=LocalDateTime.now().minusMinutes(5l);


        log.info("작동은하는가");
        Page<RealTimeIssueListDto> realTimeIssues=realTimeRepo.getrealtime(pageable,now);
        if(realTimeIssues.isEmpty()){


            return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));

        }
        List<RealTimeIssueDto> realTimeIssueDtoList=realTimeIssues.stream()
                .map(x->{

                    log.info("조회횟수:{}",x.getCount());
                    return new RealTimeIssueDto(x.getTitle(),x.getContent_id());

                })
                .collect(Collectors.toList());


        return ResponseEntity.ok(ApiResponse.success(realTimeIssueDtoList,ErrorMsgandCode.Successfind.getMsg()));
    }

    public ResponseEntity<ApiResponse<List<RealTimeIssueDto>>> getlastchagelogs(){
        LocalDateTime now=LocalDateTime.now().minusMinutes(5l);

       List<Content> contentlist=changeLongRepo.getatleastchages(now);
       log.info("{}",contentlist.size());

       List<RealTimeIssueDto> contentlist2=contentlist.stream()


               .sorted(Content.byUpdateTime())
               .limit(10)
               .map(x->{

                   return new RealTimeIssueDto(x.getTitle(),x.getContent_id());
               })
               .collect(Collectors.toList());


        return ResponseEntity.ok(ApiResponse.success(contentlist2,ErrorMsgandCode.Successfind.getMsg()));

    }


    public ResponseEntity<ApiResponse<List<RealTimeIssueDto>>> search_logic(String title){
            Pageable pageable=PageRequest.of(0,10);
            Page<Content> contents=contentRepository.search_logic(title,pageable);
            if(contents.isEmpty()){


                return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));
            }
            List<RealTimeIssueDto> realTimeIssueDtoList=contents.stream()
                    .map(x->{

                        return new RealTimeIssueDto(x.getTitle(),x.getContent_id());
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(realTimeIssueDtoList,ErrorMsgandCode.Successfind.getMsg()));

    }
}
