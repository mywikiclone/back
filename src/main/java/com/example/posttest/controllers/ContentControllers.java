package com.example.posttest.controllers;


import com.example.posttest.dtos.ChangeLogDto;
import com.example.posttest.dtos.ContentDto;
import com.example.posttest.dtos.IpDto;
import com.example.posttest.dtos.RealTimeIssueDto;
import com.example.posttest.etc.ApiResponse;
import com.example.posttest.etc.ErrorMsgandCode;
import com.example.posttest.etc.JwtUtil;
import com.example.posttest.etc.annotataion.CheckNewToken;
import com.example.posttest.etc.annotataion.LoginUser;
import com.example.posttest.service.ContentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ContentControllers {


    private final  ContentService contentService;



    private final JwtUtil jwtUtil;

    @GetMapping("/nonloginuser")
    public ResponseEntity<ApiResponse<IpDto>> getuserip(HttpServletRequest req){

            String addr=req.getRemoteAddr();

            log.info("사용자 ip:{}",addr);


            return ResponseEntity.ok(ApiResponse.success(new IpDto(addr),ErrorMsgandCode.Successfind.getMsg()));

    }

    /*
    * 글저장 하기
    */
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<ContentDto>> SaveContent(@LoginUser Long member_id,@RequestBody ContentDto contentDto/*,@CheckNewToken String newtoken*/){

        return contentService.ContentSave(contentDto,member_id);
        /*log.info("data:{} {}",contentDto.getContent(),contentDto.getTitle());
        Long memberid=optionalnumscheck(member_id,newtoken);


        return return_ans_method(contentService.ContentSave(contentDto,member_id),newtoken);*/
        //return ResponseEntity.ok(ApiResponse.success("성공", ErrorMsgandCode.Successlogin.getMsg()));
    }



    /*
    * 편집한 내용 기반으로 업데이트 및 changelog에다가 기록을 남기는애
    */
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<String>> UpdateContent(@LoginUser Long member_id,@RequestBody ContentDto contentDto/*,@CheckNewToken String newtoken*/){
        return contentService.UpdateContent(member_id,contentDto);
        /*long memberid=optionalnumscheck(member_id,newtoken);
        return return_ans_method(contentService.UpdateContent(memberid,contentDto),newtoken);*/
    }


    /*
    * 검색 칸에 제목으로 입력시 가져오는애 또한 검색했다는 기록도 남김.
    */

    @GetMapping("/search/{title}")
    public ResponseEntity<ApiResponse<ContentDto>> FindContent(@PathVariable("title") String title/*,@CheckNewToken String newtoken*/){



        ApiResponse<ContentDto> apiResponse=contentService.FindContent(title);


       /* if (apiResponse.getData() == null) {
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }프론트 단에서 false,true로 뭘 보여줄지를 판단하는게 맞을듯????*/


        return ResponseEntity.ok(apiResponse);

    }


    @GetMapping("/search/id/{id}")
    public ResponseEntity<ApiResponse<ContentDto>> FindContentById(@PathVariable("id") Long id/*,@CheckNewToken String newtoken*/){

        return contentService.FindContent(id);
        //return ResponseEntity.ok(contentService.FindContent(id));

    }



    /*
    변동 기록 살펴보는 애
    */

    @GetMapping("/changelog/{page_num}/{id}")
    public ResponseEntity<ApiResponse<List<ChangeLogDto>>>getchangelog(@PathVariable("page_num")int page_num, @PathVariable("id")Long id/*, @CheckNewToken String newtoken*/){


        return contentService.getchangelog(page_num,id);
        //return return_ans_method(contentService.getchangelog(page_num,id).newtoken);
        //return ResponseEntity.ok(contentService.getchangelog(page_num,id));

    }

    /*
    변동 기록 아이디로 변동 된text가져오는애
    */
    @GetMapping("/changelog/{id}")
    public ResponseEntity<ApiResponse<ContentDto>> getchangelogcontext(@PathVariable("id") Long id/*, @CheckNewToken String newtoken*/){

        return contentService.getchanagelogtext(id);
        //return return_ans_method(contentService.getchanagelogtext(id),newtoken);
        //return ResponseEntity.ok(contentService.getchanagelogtext(id));


    }


    /*
    * 실시간 검색어 순위 10까지 탐색해서 content의 아이디값과 title을 돌려주는애.
     */

    @GetMapping("/realtime")
    public ResponseEntity<ApiResponse<List<RealTimeIssueDto>>> getrealtime(/*@CheckNewToken String newtoken*/){


        return contentService.getRealtimeissue();
        //return return_ans_method(contentService.getRealtimeissue(),newtoken);
        //return ResponseEntity.ok(contentService.getRealtimeissue());

    }


    /*
    * 최근 바뀐 목록 보여주기*/

    @GetMapping("/lastchange")
    public ResponseEntity<ApiResponse<List<RealTimeIssueDto>>>getlastchanges(/*@CheckNewToken String newtoken*/){

        return contentService.getlastchagelogs();
        //return return_ans_method(contentService.getlastchagelogs(),newtoken);
        //return ResponseEntity.ok(contentService.getlastchagelogs());
    }


    /*
    * 검색칸에 비슷한 제목 가진애들 띄울떄 쓰는 api.*/

    @GetMapping("/searchlogic/{text}")
    public ResponseEntity<ApiResponse<List<RealTimeIssueDto>>> search_logic(@PathVariable("text") String txt/*, @CheckNewToken String newtoken*/){


        return contentService.search_logic(txt);
        //return return_ans_method(contentService.search_logic(txt),newtoken);
        //return ResponseEntity.ok(contentService.search_logic(txt));
    }

    /*
    랜덤 검색시 아무거나 주는 api
    */

   @GetMapping("/random")
   public ResponseEntity<ApiResponse<ContentDto>> random_pick(/*@CheckNewToken String newtoken*/){

       return contentService.FindContent();
       //return return_ans_method(contentService.FindContent(),newtoken);
       //return ResponseEntity.ok(contentService.FindContent());
   }

    @GetMapping("/testing")
    public void test(){
       contentService.testing();


    }


   public <T>ResponseEntity<ApiResponse<T>> return_ans_method(ApiResponse<T> apiResponse,String token){
      if(token==null){

           return ResponseEntity.ok(apiResponse);
      }

       ResponseCookie responseCookie=ResponseCookie.from("back_access_token",token)
               .maxAge(30)
               .path("/")
               .httpOnly(true)
               .secure(true)
               .sameSite("None")
               .domain("localhost")
               .build();
       HttpHeaders headers=new HttpHeaders();

       headers.add(HttpHeaders.SET_COOKIE,responseCookie.toString());

       return new ResponseEntity<>(apiResponse,headers,HttpStatus.OK);
   }



   public long optionalnumscheck(Long num, String newtoken){

       if(num!=null){

           return num;





       }


       return jwtUtil.getidfromtoken(newtoken);

   }
}
