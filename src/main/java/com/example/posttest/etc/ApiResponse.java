package com.example.posttest.etc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ApiResponse<T> {

     private boolean success_check;
     private String msg;
     private T data;


    public static <T> ApiResponse<T> success(T data,String msg){

        return new ApiResponse<>(true,msg,data);


    }

    public static <T> ApiResponse<T> fail(String msg){

        return new ApiResponse<>(false,msg,null);


    }


}
