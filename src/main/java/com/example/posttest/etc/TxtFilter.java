package com.example.posttest.etc;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class TxtFilter {


    private static String [] file_name_end_list={".jpeg",".png",".gif"};
    private static String [] file_name_black_list={"\\|",",",">","<","&","/","\\\\",":"};
    //자바의 백슬래시를 이스케이핑 처리할려면 \\가 맞다 즉 일반문자열에서는 \\가맞지만 자바에세 정규표현식을 사용하면서
    // 백슬래시1개를 이스케이핑 처리할려면 \\를 인식시켜줘야되는대 자바 문자열에서 \\를 이스케이핑 처리시켜서  "\\"로 인식시킬려면
    // \\\\이렇게써야된다.
    public static Boolean file_endname_filter(String filename){

        for(String x : file_name_end_list){


            if(filename.endsWith(x)){


                return true;
            }


        }

        return false;

    }


    public static String file_name_filter(String filename){

        for(String pattern :file_name_black_list){


            filename=filename.replaceAll(pattern,"");

        }



        return filename;

    }






}
