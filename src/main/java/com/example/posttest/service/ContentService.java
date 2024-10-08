package com.example.posttest.service;

import com.example.posttest.Exceptions.UnableToFindAccount;
import com.example.posttest.dtos.ChangeLogDto;
import com.example.posttest.dtos.ContentDto;
import com.example.posttest.dtos.RealTimeIssueDto;
import com.example.posttest.dtos.RealTimeIssueListDto;
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


    public void ContentSave(ContentDto contentDto){


            Content content=new Content(contentDto.getTitle(),contentDto.getContent());

            content.setCreate_Time(LocalDateTime.now());
            content.setUpdate_Time(LocalDateTime.now());

            contentRepository.save(content);


    }
    public ApiResponse<String> UpdateContent(Long member_id,ContentDto contentDto){
            Optional<Content> content_opt=contentRepository.findById(contentDto.getContent_id());

            LocalDateTime now=LocalDateTime.now();
            int x=contentRepository.updatecontent(contentDto.getTitle(), contentDto.getContent(),now,contentDto.getContent_id());


            Optional<Member> member=memberRepository.findById(member_id);
            log.info("content_dto,member_id:{} {}",contentDto,member_id);
            log.info("object check:{} {}",content_opt.get(),member.get());
            ChangeLog changeLog=new ChangeLog(content_opt.get(),contentDto.getContent(),member.get());
            changeLog.setCreate_Time(now);
            changeLongRepo.save(changeLog);


            return ApiResponse.success("성공",ErrorMsgandCode.Successupdate.getMsg());

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




    public ApiResponse<ContentDto> FindContent(Long id){
        Optional<Content> content=contentRepository.findById(id);

        if(content.isEmpty()){


            return ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg());
        }
        saveRealTime(content.get());
        ContentDto contentDto=new ContentDto(content.get().getContent_id(),content.get().getTitle(),content.get().getContent());
        return ApiResponse.success(contentDto,ErrorMsgandCode.Successfind.getMsg());
    }


    public ApiResponse<ContentDto> FindContent(){
        Content content=contentRepository.random_logic();
        saveRealTime(content);
        return ApiResponse.success(new ContentDto(content.getContent_id(), content.getTitle(), content.getContent()),ErrorMsgandCode.Successfind.getMsg());
    }

    public ApiResponse<ContentDto> FindContent(String title){
        Optional<Content> content=contentRepository.findbytitle(title);

        if(content.isEmpty()){


            return ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg());
        }
        saveRealTime(content.get());
        ContentDto contentDto=new ContentDto(content.get().getContent_id(),content.get().getTitle(),content.get().getContent());
        return ApiResponse.success(contentDto,ErrorMsgandCode.Successfind.getMsg());
    }





    public ApiResponse<List<ChangeLogDto>> getchangelog(int page_num, Long id){
         Pageable page=PageRequest.of(page_num,12);
         Page<ChangeLogDto> changeLogs=changeLongRepo.getchangelogs(id,page);
         log.info("최대 페이징갯수:{}",changeLogs.getTotalPages());
         if(changeLogs.isEmpty()){

             throw new UnableToFindAccount();
         }
         /*List<ChangeLogDto> changeLogDtoList=changeLogs.stream()
                 .map(x->{
                   return  new ChangeLogDto(x.getLog_Id(),x.getCreate_Time());
                 })
                 .collect(Collectors.toList());*/
         return ApiResponse.success(changeLogs.stream().toList(),ErrorMsgandCode.Successfind.getMsg());
    }

    public ApiResponse<ContentDto> getchanagelogtext(Long id){
        Optional<ChangeLog> changeLog=changeLongRepo.findById(id);
        ChangeLog c=changeLog.get();
        ContentDto contentDto=new ContentDto(c.getMember().getMember_id(),c.getContent().getTitle(),c.getChanged_Content());


        return ApiResponse.success(contentDto,ErrorMsgandCode.Successfind.getMsg());
    }


    public ApiResponse<List<RealTimeIssueDto>> getRealtimeissue(){

        Pageable pageable=PageRequest.of(0,10);
        LocalDateTime now=LocalDateTime.now().minusMinutes(5l);



        Page<RealTimeIssueListDto> realTimeIssues=realTimeRepo.getrealtime(pageable,now);

        List<RealTimeIssueDto> realTimeIssueDtoList=realTimeIssues.stream()
                .map(x->{

                    log.info("조회횟수:{}",x.getCount());
                    return new RealTimeIssueDto(x.getTitle(),x.getContent_id());

                })
                .collect(Collectors.toList());


        return ApiResponse.success(realTimeIssueDtoList,ErrorMsgandCode.Successfind.getMsg());
    }

    public ApiResponse<List<RealTimeIssueDto>> getlastchagelogs(){
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


        return ApiResponse.success(contentlist2,ErrorMsgandCode.Successfind.getMsg());

    }


    public ApiResponse<List<RealTimeIssueDto>> search_logic(String title){
            Pageable pageable=PageRequest.of(0,10);
            Page<Content> contents=contentRepository.search_logic(title,pageable);

            List<RealTimeIssueDto> realTimeIssueDtoList=contents.stream()
                    .map(x->{

                        return new RealTimeIssueDto(x.getTitle(),x.getContent_id());
                    })
                    .collect(Collectors.toList());

            return ApiResponse.success(realTimeIssueDtoList,ErrorMsgandCode.Successfind.getMsg());

    }
}
