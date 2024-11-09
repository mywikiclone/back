package com.example.posttest.service;

import com.example.posttest.Exceptions.*;
import com.example.posttest.dtos.*;
import com.example.posttest.entitiy.ChangeLog;
import com.example.posttest.entitiy.Content;
import com.example.posttest.entitiy.Member;
import com.example.posttest.entitiy.RealTimeIssue;
import com.example.posttest.etc.ApiResponse;
import com.example.posttest.etc.ErrorMsgandCode;
import com.example.posttest.etc.TxtFilter;
import com.example.posttest.etc.UserAdmin;
import com.example.posttest.repository.changelogrepo.ChangeLongRepo;
import com.example.posttest.repository.contentrepositories.ContentRepository;
import com.example.posttest.repository.memrepo.MemberRepository;
import com.example.posttest.repository.realtimerepo.RealTimeRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.tika.Tika;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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

    private final ResourceLoader resourceLoader;


    public ResponseEntity<ApiResponse<String>> uploadfile(MultipartFile file){


        Tika tiKa=new Tika();

        try {
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

            if(file.getSize()>maxSize){

                throw new EtcError();
            }


            String mimetype=tiKa.detect(file.getInputStream());

            if(mimetype.equals("image/jpg")||mimetype.equals("image/png")||mimetype.equals("image/gif")) {


                file.transferTo(destination);


                return ResponseEntity.ok(ApiResponse.success("성공", ErrorMsgandCode.Successfind.getMsg()));
            }


            throw new EtcError();

        }
        catch(EtcError e){


            throw new EtcError();
        }

        catch (Exception e){


            log.info("e:{}",e.getMessage());

            throw new CantFindError();
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
               ;
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



    public ResponseEntity<ApiResponse<ContentDto>> ContentSave(ContentDto contentDto, Long member_id){

            try {
                Optional<Member> member = memberRepository.findById(member_id);

                Content content = new Content(member.get(), contentDto.getTitle(), contentDto.getContent(), UserAdmin.User);

                content.setCreate_Time(LocalDateTime.now());
                content.setUpdate_Time(LocalDateTime.now());

                Content content1 = contentRepository.save(content);
                log.info("content1:{} {} {} {} {}", content1.getTitle(), content1.getContent_id(), content1.getUpdate_Time(), content.getContent(), content1.getMember().getEmail());

                return ResponseEntity.ok(ApiResponse.success(new ContentDto(content1.getContent_id(), content1.getTitle(), content1.getContent(), content1.getMember().getEmail(), content1.getUpdate_Time()), ErrorMsgandCode.Successfind.getMsg()));
            }
            catch (Exception e){

                throw new EtcError();
        }
    }


    public ResponseEntity<ApiResponse<String>> UpdateContent(Long member_id,ContentDto contentDto){
            Optional<Content> content_opt=contentRepository.findById(contentDto.getContent_id());




            LocalDateTime now=LocalDateTime.now();
            if(content_opt.isPresent()){
                Optional<Member> member=memberRepository.findById(member_id);

                if(gradecheck(content_opt.get(),member.get())){
                    int x = contentRepository.updatecontent(contentDto.getTitle(), contentDto.getContent(), now, contentDto.getContent_id());

                    log.info("content_dto,member_id:{} {}", contentDto, member_id);
                    log.info("object check:{} {}", content_opt.get(), member.get());
                    ChangeLog changeLog = new ChangeLog(content_opt.get(), contentDto.getContent(), member.get());
                    changeLog.setCreate_Time(now);
                    changeLongRepo.save(changeLog);
                    return ResponseEntity.ok(ApiResponse.success(now.toString(), ErrorMsgandCode.Successupdate.getMsg()));

                }


                throw new CantFindError();

            }

            else{

                throw new CantFindDataError();
            }



            //return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));

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

            throw new CantFindDataError();
            //return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));
        }
        saveRealTime(content.get());
        ContentDto contentDto=new ContentDto(content.get().getContent_id(),content.get().getTitle(),content.get().getContent(),content.get().getMember().getEmail(),content.get().getUpdate_Time(),content.get().getGrade().name());
        return ResponseEntity.ok(ApiResponse.success(contentDto,ErrorMsgandCode.Successfind.getMsg()));
    }


    public ResponseEntity<ApiResponse<ContentDto>> FindContent(){
        Optional<Content> content=contentRepository.random_logic();

        if(content.isPresent()) {
            saveRealTime(content.get());
            return ResponseEntity.ok(ApiResponse.success(new ContentDto(content.get().getContent_id(),content.get().getTitle(),content.get().getContent(),content.get().getMember().getEmail(),content.get().getUpdate_Time(),content.get().getGrade().name()), ErrorMsgandCode.Successfind.getMsg()));
        }

        throw new CantFindDataError();
        //return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));
    }

    public ApiResponse<ContentDto> FindContent(String title){
        Optional<Content> content=contentRepository.findbytitle(title);

        if(content.isEmpty()){

            throw new CantFindDataError();
            //return ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg());
        }
        saveRealTime(content.get());
        ContentDto contentDto=new ContentDto(content.get().getContent_id(),content.get().getTitle(),content.get().getContent(),content.get().getMember().getEmail(),content.get().getUpdate_Time(),content.get().getGrade().name());
        return ApiResponse.success(contentDto,ErrorMsgandCode.Successfind.getMsg());
    }




    public ResponseEntity<ApiResponse<List<ChangeLogListDto>>> getchangelog(int page_num){


        Pageable page=PageRequest.of(page_num,12);
        Page<ChangeLogListDto> changeLogs=changeLongRepo.getchangelogs(page);
        if(changeLogs.isEmpty()){
            throw new CantFindError();
           // return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));
        }

        if(changeLogs.isLast()){

            return ResponseEntity.ok(ApiResponse.success(changeLogs.stream().toList(),"마지막 페이지"));

        }



        return ResponseEntity.ok(ApiResponse.success(changeLogs.stream().toList(),ErrorMsgandCode.Successfind.getMsg()));

    }



    public ResponseEntity<ApiResponse<List<ChangeLogDto>>> getchangelog(int page_num, Long id){
         Pageable page=PageRequest.of(page_num,12);
         Page<ChangeLogDto> changeLogs=changeLongRepo.getchangelogs(id,page);
         log.info("최대 페이징갯수:{}",changeLogs.getTotalPages());
         if(changeLogs.isEmpty()){


             throw new CantFindError();
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

    public ResponseEntity<ApiResponse<ContentDto>> getchanagelogtext(Long id){
        Optional<ChangeLog> changeLog=changeLongRepo.findById(id);



        if(changeLog.isPresent()){
        ChangeLog c=changeLog.get();
        ContentDto contentDto=new ContentDto(c.getMember().getMember_id(),c.getContent().getTitle(),c.getChanged_Content(),c.getUpdate_Time());


        return ResponseEntity.ok(ApiResponse.success(contentDto,ErrorMsgandCode.Successfind.getMsg()));}
        //return ResponseEntity.ok(ApiResponse.fail(ErrorMsgandCode.Failfind.getMsg()));


        throw new CantFindDataError();

    }



    public void testing(){

    Optional<RealTimeIssue> rel=realTimeRepo.getreal();
    log.info("rel:{}",rel);
    }



    public ResponseEntity<ApiResponse<List<RealTimeIssueDto>>> getRealtimeissue(){

        Pageable pageable=PageRequest.of(0,10);
        LocalDateTime now=LocalDateTime.now().minusMinutes(5l);


        log.info("realtime작동");
        Page<RealTimeIssueListDto> realTimeIssues=realTimeRepo.getrealtime(pageable,now);
        log.info("realtime작동:{}",realTimeIssues.isEmpty());
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
        UserAdmin content_grade=content.getGrade();

        UserAdmin member_grade=content.getGrade();



        if(content_grade.getGrade()>member_grade.getGrade()){


            return false;
        }


        return true;


    }
}
