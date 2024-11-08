package com.example.posttest.etc;

public enum UserAdmin {

    User(1L),Admin(2L),Ban(0L);

    private final long grade;


    UserAdmin(Long grade){
        this.grade=grade;
    }

    public Long getGrade(){


        return grade;
    }
}
