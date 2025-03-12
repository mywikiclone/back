package com.example.posttest.controllers;


import com.example.posttest.dtos.UserSessionTot;
import com.example.posttest.etc.ApiResponse;
import com.example.posttest.etc.annotataion.LoginUser;

import com.example.posttest.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;


    @GetMapping("/testing_xml")
    public String testing(){



       return  feedService.makedata();

    }

    @GetMapping("/makefeed/{topic_id}")
    public ResponseEntity<ApiResponse<String>> makefeed(@LoginUser UserSessionTot userSessionTot, @PathVariable("topic_id") Long topc_id){



        return feedService.create_feed(userSessionTot.getUserSession().getMember_id(),topc_id);
    }

    @GetMapping("/canclefeed/{topic_id}")
    public ResponseEntity<ApiResponse<String>> canclefeed(@LoginUser UserSessionTot userSessionTot, @PathVariable("topic_id") Long topc_id){



        return feedService.cancel_feed(userSessionTot.getUserSession().getMember_id(),topc_id);
    }



    @GetMapping("/feedstate/{topic_id}")
    public ResponseEntity<ApiResponse<String>> feedstate(@LoginUser UserSessionTot usersessiontot, @PathVariable("topic_id") Long topc_id){



        return feedService.get_feed(usersessiontot.getUserSession().getMember_id(),topc_id);

    }

}
