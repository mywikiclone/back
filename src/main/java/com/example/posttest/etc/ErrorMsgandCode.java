package com.example.posttest.etc;

public enum ErrorMsgandCode {
    //성공 메시지
    Successsave("성공적으로 저장하였습니다."),
    Successupdate("성공적으로 업데이트하였습니다."),
    Successfind("검색 성공하였습니다"),
    Successlogin("로그인 성공"),


    //실패
    Failsave("저장을 실패했습니다."),
    Failupdate("업데이트를 실패했습니다."),
    Failfind("해당되는 것이 없습니다"),
    FailJwt("적절치 않은 토큰입니다."),
    FailIdExist("이미존재하는 회원입니다"),
    FailNoExistMember("찾을수없는 회원입니다"),
    Fail_Need_ReLogin("로그인이 필요합니다"),
    Fail_Csrf_Auth("csrf 실패 재로그인 해주세요"),

    Fail_Find("요청하신 자료가 없습니다"),

    Fail_Etc_Error("오류가 발생했습니다 다시시도해주세요"),


    Fail_Find_Data("해당되는 데이터가없습니다"),

    Fail_No_Power("권한 부족!"),


    Fail_Access_Excced_Error("입력횟수를 초과했습니다");





    private String msg;

    ErrorMsgandCode(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
