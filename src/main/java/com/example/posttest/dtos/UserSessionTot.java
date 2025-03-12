package com.example.posttest.dtos;


import jakarta.servlet.http.Cookie;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserSessionTot {


    private String session_id;

    private UserSession userSession;



    public UserSessionTot(String session_id, UserSession userSession) {
        this.session_id = session_id;
        this.userSession = userSession;

    }
}
