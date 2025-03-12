package com.example.posttest.etc;

import com.example.posttest.Exceptions.EtcError;
import com.example.posttest.Exceptions.ReLoginError;
import com.example.posttest.dtos.UserSession;
import com.example.posttest.dtos.UserSessionTot;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class CookieRedisSession {

    private final RedisTemplate<String,String> redisTemplate;

    private final JwtUtil jwtUtil;

    private final ObjectMapper objectMapper;


    public UserSessionTot getusersessiontot(HttpServletRequest req){

        Cookie cookie=getcookie(req);

        if(cookie==null){
            throw new ReLoginError();
        }
        String session_id=get_session_id_from_cookie(cookie);


        UserSession userSession=getusersession(session_id);

        return new UserSessionTot(session_id,userSession);
    }

    private Cookie getcookie(HttpServletRequest req){

        Cookie[] cookies=req.getCookies();
        if(cookies!=null) {
            Optional<Cookie> cookie = Arrays.stream(cookies).filter(x -> "JSESSIONID2".equals(x.getName()))
                    .findFirst();


            if(cookie.isPresent()){

                return cookie.get();}


            return null;
        }

        return null;


    }
    private UserSession getusersession(String session_id){

        try {
            String user_data=(String) redisTemplate.opsForValue().get(session_id);

            UserSession userSession=Optional.ofNullable(user_data)
                    .map(x->{
                        try{


                            return objectMapper.readValue(x,UserSession.class);

                        }
                        catch(Exception e){


                            return null;
                        }



                    }).orElse(null);




            return userSession;

        }
        catch(Exception e){

            throw new ReLoginError();

        }
    }
    private String get_session_id_from_cookie(Cookie cookie){
        return cookie.getValue();
    }

    public String [] makeyusersession(Long memberid){

        try {
            String session_key = UUID.randomUUID().toString();

            String csrf = jwtUtil.genjwt();
            UserSession userSession = new UserSession(memberid, csrf);

            redisTemplate.opsForValue().set(session_key,objectMapper.writeValueAsString(userSession),1800L, TimeUnit.SECONDS);


            return new String[] {session_key,csrf};

        }


        catch (Exception e){

            throw new EtcError();
        }

    }


  public String delete_user_session_tot(UserSessionTot userSessionTot){


        redisTemplate.delete(userSessionTot.getSession_id());


        return userSessionTot.getSession_id();

  }



  public String extend_user_session_tot(UserSessionTot userSession){



        redisTemplate.expire(userSession.getSession_id(),1800L,TimeUnit.SECONDS);

        return userSession.getSession_id();

  }


  public HttpHeaders makecookieinheader(UserSessionTot userSession,String key){

        String session_id="";
        if(key=="extend") {
            session_id = extend_user_session_tot(userSession);
        }
        else{

            session_id=delete_user_session_tot(userSession);
        }
        HttpHeaders headers=new HttpHeaders();

      ResponseCookie responseCookie=ResponseCookie.from("JSESSIONID2",session_id)
              .secure(true)
              .httpOnly(true)
              .maxAge(key.equals("extend") ? 1800 : 0)
              .domain("localhost")
              .sameSite("none")
              .path("/")
              .build();



        /*ResponseCookie responseCookie=ResponseCookie.from("JSESSIONID",cookie.getValue())
                .secure(true)
                .httpOnly(true)
                .maxAge(key.equals("extend") ? 1800 : 0)
                .domain(".mywikiback.shop")
                .sameSite("strict")
                .path("/")
                .build();*/

        headers.add(HttpHeaders.SET_COOKIE,responseCookie.toString());


        return headers;
    }





}


