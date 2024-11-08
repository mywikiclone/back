package com.example.posttest.controllers;


import com.example.posttest.dtos.AdminContentDto;
import com.example.posttest.dtos.AdminMemberDto;
import com.example.posttest.dtos.ContentDto;
import com.example.posttest.dtos.MemberDto;
import com.example.posttest.entitiy.Member;
import com.example.posttest.etc.ApiResponse;
import com.example.posttest.etc.annotataion.LoginUser;
import com.example.posttest.repository.memrepo.MemberRepository;
import com.example.posttest.service.AdminService;
import com.example.posttest.service.ContentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class AdminController {



    private final AdminService adminService;



    @PostMapping("/admin/changeuser")
    public ResponseEntity<ApiResponse<String>> changeuseradmin(@LoginUser Long user_id,@RequestBody AdminMemberDto adminMemberDto){


            return adminService.changeuseradmin(user_id,adminMemberDto);

    }

    @PostMapping("/admin/contentadmin")
    public ResponseEntity<ApiResponse<String>> changecontentadmin(@LoginUser Long user_id,@RequestBody AdminContentDto adminContentDto){

       return  adminService.changecontentadmin(user_id,adminContentDto);


    }


    @GetMapping("/admin/check")
    public ResponseEntity<ApiResponse<String>> checkadmin(@LoginUser Long user_id){


            return adminService.checkadmin(user_id);

    }

    @GetMapping("/admin/contentlist")
    public ResponseEntity<ApiResponse<List<ContentDto>>> contentlist(@LoginUser Long user_id){


        return adminService.getcontentlist(user_id);

    }


    @GetMapping("/admin/content/{title}")
    public ResponseEntity<ApiResponse<ContentDto>> findcontent(@LoginUser Long user_id,@PathVariable(name = "title")String title){




        return adminService.findcontent(user_id,title);
    }


    @PostMapping("/admin/finduser")
    public ResponseEntity<ApiResponse<AdminMemberDto>> finduser(@LoginUser Long user_id,@RequestBody MemberDto memberDto){


        return adminService.findmeber(user_id,memberDto);

    }




}
