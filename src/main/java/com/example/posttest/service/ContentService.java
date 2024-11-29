package com.example.posttest.service;

import com.example.posttest.Exceptions.*;
import com.example.posttest.dtos.*;
import com.example.posttest.entitiy.*;
import com.example.posttest.etc.*;
import com.example.posttest.repository.ContentAdminRepo;
import com.example.posttest.repository.LobRepository;
import com.example.posttest.repository.changelogrepo.ChangeLongRepo;
import com.example.posttest.repository.contentrepositories.ContentRepository;
import com.example.posttest.repository.memrepo.MemberRepository;
import com.example.posttest.repository.realtimerepo.RealTimeRepo;
import jakarta.persistence.Lob;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.tika.Tika;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.cglib.core.Local;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {


    private final ContentAdminRepo contentAdminRepo;
    private final ContentRepository contentRepository;
    private final ChangeLongRepo changeLongRepo;
    private final RealTimeRepo realTimeRepo;
    private final MemberRepository memberRepository;
    private final ResourceLoader resourceLoader;
    private final CookieRedisSession cookieRedisSession;
    private final LobRepository lobRepository;
    public ResponseEntity<ApiResponse<String>> uploadfile(MultipartFile file){


        Tika tiKa=new Tika();


        try(InputStream inputStream=file.getInputStream()){
            if(!TxtFilter.file_endname_filter(file.getOriginalFilename())){
                throw new CantFindError();

            }



            String filename = TxtFilter.file_name_filter(file.getOriginalFilename());



            String filepath = System.getProperty("user.dir")+"/src/main/resources/uploads/" + filename;

            File destination = new File(filepath);
            log.info("filepath:{}",filepath);
            log.info("exist:{}",destination.exists());

            if(destination.exists()){

                throw new EtcError();

            }



            long maxSize = 2 * 1024 * 1024;
            log.info("파일크기:{}",file.getSize());
            log.info("더크나ㅑ?:{}",file.getSize()>maxSize);
            if(file.getSize()>maxSize){
                log.info("에러발생");
                throw new EtcError();
            }

            String mimetype=tiKa.detect(inputStream);
            log.info("mimetype:{}",mimetype);
            log.info("equal:{}",mimetype.equals("image/png"));
            if(mimetype.equals("image/jpg")||mimetype.equals("image/png")||mimetype.equals("image/gif")) {

                log.info("????");

                file.transferTo(destination);


                return ResponseEntity.ok(ApiResponse.success("성공", ErrorMsgandCode.Successfind.getMsg()));
            }


            throw new EtcError();

        }
        catch(Exception e){


            throw new EtcError();
        }





    }



    public ResponseEntity<Resource> getimgs(String filename){


            filename = TxtFilter.file_name_filter(filename);

            String [] end_name={"jpeg","png","jpg","gif"};

            String file_search_name="";
            for(String end:end_name) {
                file_search_name=String.format("/uploads/%s.%s", filename, end);
               // file_search_name = String.format(System.getProperty("user.dir")+"/src/main/resources/uploads/%s.%s", filename, end);
               // file_search_name=file_search_name.replaceAll("/","\\\\");

                Resource resource = resourceLoader.getResource("classpath:"+file_search_name);

                if (!resource.exists()) {
                    continue;

                }


                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_TYPE, String.format("image/%s", end));
                return new ResponseEntity<>(resource, headers, HttpStatus.OK);

            }
      //  file_search_name=System.getProperty("user.dir")+"/src/main/resources/uploads/basic.jpg";
       // file_search_name=file_search_name.replaceAll("/","\\\\");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, String.format("image/%s","jpeg"));
        Resource resource = resourceLoader.getResource("classpath:"+"/uploads/basic.jpg");
        log.info("결국 이렇게되느건가:?{}",resource.exists());
        return new ResponseEntity<>(resource,headers,HttpStatus.OK);

    }




    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<ApiResponse<ContentDto>> ContentSave(ContentDto contentDto, UserSessionTot userSessionTot){
            LocalDateTime now=LocalDateTime.now();
            long member_id=userSessionTot.getUserSession().getMember_id();


                Optional<Member> member = memberRepository.findById(member_id);

                LobContent lobcontent=new LobContent(contentDto.getContent());

                lobcontent=lobRepository.save(lobcontent);


                ContentAdmin contentAdmin=new ContentAdmin(UserAdmin.User);
                contentAdminRepo.save(contentAdmin);

                Content content = new Content(member.get(), contentDto.getTitle(), lobcontent, contentAdmin);

                content.setCreate_Time(now);
                content.setUpdate_Time(now);

                Content content1 = contentRepository.save(content);


                ChangeLog changeLog=new ChangeLog(content,lobcontent,member.get());

                changeLog.setCreate_Time(now);
                changeLongRepo.save(changeLog);

                HttpHeaders headers=cookieRedisSession.makecookieinheader(userSessionTot,"extend");




                return new ResponseEntity(ApiResponse.success(new ContentDto(content1.getContent_id(), content1.getTitle(), lobcontent.getContent(), content1.getMember().getEmail(), content1.getUpdate_Time()), ErrorMsgandCode.Successfind.getMsg()),headers,HttpStatus.OK);


    }





    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ResponseEntity<ApiResponse<String>> UpdateContent(UserSessionTot userSessionTot,ContentDto contentDto){

            long member_id=userSessionTot.getUserSession().getMember_id();;
            Optional<Content> content_opt=contentRepository.findbyidwithforupdate(contentDto.getContent_id());

            LocalDateTime now=LocalDateTime.now();
            if(content_opt.isPresent()){
                Optional<Member> member=memberRepository.findById(member_id);


                if(gradecheck(content_opt.get(),member.get())){

                    LobContent lobContent=new LobContent(contentDto.getContent());
                    lobContent=lobRepository.save(lobContent);
                    content_opt.get().setLobContent(lobContent);
                    content_opt.get().setCreate_Time(now);
                    contentRepository.save(content_opt.get());
                    ChangeLog changeLog = new ChangeLog(content_opt.get(),lobContent, member.get());
                    changeLog.setCreate_Time(now);
                    changeLongRepo.save(changeLog);
                    HttpHeaders headers=cookieRedisSession.makecookieinheader(userSessionTot,"extend");
                    return new ResponseEntity(ApiResponse.success(now.toString(), ErrorMsgandCode.Successupdate.getMsg()),headers,HttpStatus.OK);



                }


                throw new AdminError();

            }

            throw new CantFindError();





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





    @Transactional
    public ResponseEntity<ApiResponse<ContentDto>> FindContent(Long id){


        Optional<Content> content=contentRepository.findById(id);

        if(content.isEmpty()){

            throw new CantFindError();
            //return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));
        }
        saveRealTime(content.get());
        ContentDto contentDto=new ContentDto(content.get().getContent_id(),content.get().getTitle(),content.get().getLobContent().getContent(),content.get().getMember().getEmail(),content.get().getUpdate_Time(),content.get().getGrade().getGrade().name());
        return ResponseEntity.ok(ApiResponse.success(contentDto,ErrorMsgandCode.Successfind.getMsg()));
    }


    @Transactional
    public ResponseEntity<ApiResponse<ContentDto>> FindContent(){
        Optional<Content> content=contentRepository.random_logic();

        if(content.isPresent()) {
            saveRealTime(content.get());
            return ResponseEntity.ok(ApiResponse.success(new ContentDto(content.get().getContent_id(),content.get().getTitle(),content.get().getLobContent().getContent(),content.get().getMember().getEmail(),content.get().getUpdate_Time(),content.get().getGrade().getGrade().name()), ErrorMsgandCode.Successfind.getMsg()));
        }

        throw new CantFindError();
        //return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));
    }

    @Transactional
    public ApiResponse<ContentDto> FindContent(String title){
        Optional<Content> content=contentRepository.findbytitle(title);

        if(content.isEmpty()){

            throw new CantFindError();
            //return ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg());
        }
        saveRealTime(content.get());
        ContentDto contentDto=new ContentDto(content.get().getContent_id(),content.get().getTitle(),content.get().getLobContent().getContent(), content.get().getMember().getEmail(),content.get().getUpdate_Time(),content.get().getGrade().getGrade().name());
        return ApiResponse.success(contentDto,ErrorMsgandCode.Successfind.getMsg());
    }



    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<ChangeLogListDto>>> getchangelog(int page_num){


        Pageable page=PageRequest.of(page_num,12);
        Page<ChangeLogListDto> changeLogs=changeLongRepo.getchangelogs(page);
        if(changeLogs.isEmpty()){
           // throw new CantFindError();
            List<ChangeLogListDto> list=new ArrayList<>();
            return ResponseEntity.ok(ApiResponse.success(list,"마지막 페이지"));
            //return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));
        }

        if(changeLogs.isLast()){

            return ResponseEntity.ok(ApiResponse.success(changeLogs.stream().toList(),"마지막 페이지"));

        }



        return ResponseEntity.ok(ApiResponse.success(changeLogs.stream().toList(),ErrorMsgandCode.Successfind.getMsg()));

    }


    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<ChangeLogDto>>> getchangelog(int page_num, Long id){
         Pageable page=PageRequest.of(page_num,12);
         Page<ChangeLogDto> changeLogs=changeLongRepo.getchangelogs(id,page);
         log.info("최대 페이징갯수:{}",changeLogs.getTotalPages());
         if(changeLogs.isEmpty()){

            List<ChangeLogDto> list=new ArrayList<>();

             return ResponseEntity.ok(ApiResponse.success(list,"마지막 페이지"));
            // throw new CantFindError();
             //return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));
         }

        if(changeLogs.isLast()){

            return ResponseEntity.ok(ApiResponse.success(changeLogs.stream().toList(),"마지막 페이지"));

        }




         /*List<ChangeLogDto> changeLogDtoList=changeLogs.stream()
                 .map(x->{
                   return  new ChangeLogDto(x.getLog_Id(),x.getCreate_Time());
                 })
                 .collect(Collectors.toList());*/
         return ResponseEntity.ok(ApiResponse.success(changeLogs.stream().toList(),ErrorMsgandCode.Successfind.getMsg()));
    }




    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<ContentDto>> getchanagelogtext(Long id){
        Optional<ChangeLog> changeLog=changeLongRepo.findById(id);



        if(changeLog.isPresent()){
        ChangeLog c=changeLog.get();
        Content content=c.getContent();
        ContentDto contentDto=new ContentDto(content.getContent_id(),c.getContent().getTitle(),c.getLobContent().getContent(),c.getMember().getEmail(),c.getUpdate_Time(),content.getGrade().getGrade().name());



        return ResponseEntity.ok(ApiResponse.success(contentDto,ErrorMsgandCode.Successfind.getMsg()));}
        //return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));


        throw new CantFindError();

    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<RealTimeIssueDto>>> getRealtimeissue(){

        Pageable pageable=PageRequest.of(0,10);
        LocalDateTime now=LocalDateTime.now().minusMinutes(5l);



        Page<RealTimeIssueListDto> realTimeIssues=realTimeRepo.getrealtime(pageable,now);

        if(realTimeIssues.isEmpty()){

            throw new CantFindError();
           // return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));

        }
        List<RealTimeIssueDto> realTimeIssueDtoList=realTimeIssues.stream()
                .map(x->{

                    log.info("조회횟수:{}",x.getCount());
                    return new RealTimeIssueDto(x.getTitle(),x.getContent_id());

                })
                .collect(Collectors.toList());


        return ResponseEntity.ok(ApiResponse.success(realTimeIssueDtoList,ErrorMsgandCode.Successfind.getMsg()));
    }



    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<RealTimeIssueDto>>> search_logic(String title){
            Pageable pageable=PageRequest.of(0,10);
            Page<Content> contents=contentRepository.search_logic(title,pageable);
            if(contents.isEmpty()){




                throw new CantFindError();
                //return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));
            }
            List<RealTimeIssueDto> realTimeIssueDtoList=contents.stream()
                    .map(x->{

                        return new RealTimeIssueDto(x.getTitle(),x.getContent_id());
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(realTimeIssueDtoList,ErrorMsgandCode.Successfind.getMsg()));

    }





    public boolean  gradecheck(Content content,Member member){
        UserAdmin content_grade=content.getGrade().getGrade();

        UserAdmin member_grade=member.getGrade().getGrade();



        if(content_grade.getGrade()>member_grade.getGrade()){


            return false;
        }


        return true;


    }
}
