package com.example.posttest.controllers;


import com.example.posttest.dtos.*;
import com.example.posttest.entitiy.Member;
import com.example.posttest.etc.ApiResponse;
import com.example.posttest.etc.annotataion.LoginUser;
import com.example.posttest.repository.memrepo.MemberRepository;
import com.example.posttest.service.AdminService;
import com.example.posttest.service.ContentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@Controller
@Slf4j
@RequiredArgsConstructor
public class AdminController {



    private final AdminService adminService;



    @PostMapping("/admin/changeuser")
    public ResponseEntity<ApiResponse<String>> changeuseradmin(@LoginUser UserSessionTot userSessionTot, @RequestBody AdminMemberDto adminMemberDto){


            return adminService.changeuseradmin(userSessionTot,adminMemberDto);

    }

    @PostMapping("/admin/changecotent")
    public ResponseEntity<ApiResponse<String>> changecontentadmin(@LoginUser UserSessionTot userSessionTot,@RequestBody AdminContentDto adminContentDto){

       return  adminService.changecontentadmin(userSessionTot,adminContentDto);


    }


    @GetMapping("/admin/check")
    public ResponseEntity<ApiResponse<String>> checkadmin(@LoginUser UserSessionTot userSessionTot){

            log.info("amdin check:{}",userSessionTot);
            return adminService.checkadmin(userSessionTot);

    }

    @GetMapping("/admin/contentlist")
    public ResponseEntity<ApiResponse<List<ContentDto>>> contentlist(@LoginUser UserSessionTot userSessionTot){


        return adminService.getcontentlist(userSessionTot);

    }


    @GetMapping("/admin/content/{title}")
    public ResponseEntity<ApiResponse<ContentDto>> findcontent(@LoginUser UserSessionTot userSessionTot,@PathVariable(name = "title")String title){




        return adminService.findcontent(userSessionTot,title);
    }


    @PostMapping("/admin/finduser")
    public ResponseEntity<ApiResponse<AdminMemberDto>> finduser(@LoginUser UserSessionTot userSessionTot,@RequestBody MemberDto memberDto){


        return adminService.findmeber(userSessionTot,memberDto);

    }




}
