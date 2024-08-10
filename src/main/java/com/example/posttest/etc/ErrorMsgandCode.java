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
    Fail_Need_ReLogin("로그인이 필요합니다");



    private String msg;

    ErrorMsgandCode(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
