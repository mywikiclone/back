package com.example.posttest.dtos;


import jakarta.servlet.http.Cookie;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserSessionTot {


    private String session_id;

    private UserSession userSession;

    private Cookie cookie;

    public UserSessionTot(String session_id, UserSession userSession, Cookie cookie) {
        this.session_id = session_id;
        this.userSession = userSession;
        this.cookie = cookie;
    }
}
