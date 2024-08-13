package com.example.posttest.controllers;


import com.example.posttest.dtos.ChangeLogDto;
import com.example.posttest.dtos.ContentDto;
import com.example.posttest.dtos.RealTimeIssueDto;
import com.example.posttest.etc.ApiResponse;
import com.example.posttest.etc.ErrorMsgandCode;
import com.example.posttest.etc.annotataion.CheckNewToken;
import com.example.posttest.etc.annotataion.LoginUser;
import com.example.posttest.service.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ContentControllers {


    private final ContentService contentService;


    /*
    * 글저장 하기
    */
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<String>> SaveContent(@RequestBody ContentDto contentDto,@CheckNewToken String newtoken){

        contentService.ContentSave(contentDto);

        return return_ans_method(ApiResponse.success("성공",ErrorMsgandCode.Successlogin.getMsg()),newtoken);
        //return ResponseEntity.ok(ApiResponse.success("성공", ErrorMsgandCode.Successlogin.getMsg()));
    }



    /*
    * 편집한 내용 기반으로 업데이트 및 changelog에다가 기록을 남기는애
    */
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<String>> UpdateContent(@LoginUser Long member_id,@RequestBody ContentDto contentDto,@CheckNewToken String newtoken){
        return return_ans_method(contentService.UpdateContent(member_id,contentDto),newtoken);
    }


    /*
    * 검색 칸에 제목으로 입력시 가져오는애 또한 검색했다는 기록도 남김.
    */

    @GetMapping("/search/{title}")
    public ResponseEntity<ApiResponse<ContentDto>> FindContent(@PathVariable("title") String title,@CheckNewToken String newtoken){

        ApiResponse<ContentDto> apiResponse=contentService.FindContent(title);


        if(apiResponse.getData()==null){
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }


        return ResponseEntity.ok(apiResponse);

    }


    @GetMapping("/search/id/{id}")
    public ResponseEntity<ApiResponse<ContentDto>> FindContentById(@PathVariable("id") Long id,@CheckNewToken String newtoken){

        return return_ans_method(contentService.FindContent(id),newtoken);
        //return ResponseEntity.ok(contentService.FindContent(id));

    }



    /*
    변동 기록 살펴보는 애
    */

    @GetMapping("/changelog/{page_num}/{id}")
    public ResponseEntity<ApiResponse<List<ChangeLogDto>>>getchangelog(@PathVariable("page_num")int page_num, @PathVariable("id")Long id, @CheckNewToken String newtoken){

        return return_ans_method(contentService.getchangelog(page_num,id),newtoken);
        //return ResponseEntity.ok(contentService.getchangelog(page_num,id));

    }

    /*
    변동 기록 아이디로 변동 된text가져오는애
    */
    @GetMapping("/changelog/{id}")
    public ResponseEntity<ApiResponse<String>> getchangelogcontext(@PathVariable("id") Long id, @CheckNewToken String newtoken){


        return return_ans_method(contentService.getchanagelogtext(id),newtoken);
        //return ResponseEntity.ok(contentService.getchanagelogtext(id));


    }


    /*
    * 실시간 검색어 순위 10까지 탐색해서 content의 아이디값과 title을 돌려주는애.
     */

    @GetMapping("/realtime")
    public ResponseEntity<ApiResponse<List<RealTimeIssueDto>>> getrealtime(@CheckNewToken String newtoken){
        return return_ans_method(contentService.getRealtimeissue(),newtoken);
        //return ResponseEntity.ok(contentService.getRealtimeissue());

    }


    /*
    * 최근 바뀐 목록 보여주기*/

    @GetMapping("/lastchange")
    public ResponseEntity<ApiResponse<List<RealTimeIssueDto>>>getlastchanges(@CheckNewToken String newtoken){


        return return_ans_method(contentService.getlastchagelogs(),newtoken);
        //return ResponseEntity.ok(contentService.getlastchagelogs());
    }


    /*
    * 검색칸에 비슷한 제목 가진애들 띄울떄 쓰는 api.*/

    @GetMapping("/searchlogic/{text}")
    public ResponseEntity<ApiResponse<List<RealTimeIssueDto>>> search_logic(@PathVariable("text") String txt, @CheckNewToken String newtoken){

        return return_ans_method(contentService.search_logic(txt),newtoken);
        //return ResponseEntity.ok(contentService.search_logic(txt));
    }

    /*
    랜덤 검색시 아무거나 주는 api
    */

   @GetMapping("/random")
   public ResponseEntity<ApiResponse<ContentDto>> random_pick(@CheckNewToken String newtoken){
       return return_ans_method(contentService.FindContent(),newtoken);
       //return ResponseEntity.ok(contentService.FindContent());
   }




   public <T>ResponseEntity<ApiResponse<T>> return_ans_method(ApiResponse<T> apiResponse,String token){
      if(token==null){
           log.info("널값...?");
           return ResponseEntity.ok(apiResponse);
      }
       log.info("헤더추가!!!");
      HttpHeaders headers=new HttpHeaders();
       headers.add("NewGenToken",token);

     return new ResponseEntity<>(apiResponse,headers,HttpStatus.OK);
   }
}
