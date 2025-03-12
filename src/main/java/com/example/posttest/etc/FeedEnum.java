package com.example.posttest.etc;

public enum FeedEnum {
    OK("OK"),NOT("NOT");


    private final String state;


    FeedEnum(String state){
        this.state=state;
    }

    public String getState(){


        return state;
    }
}
