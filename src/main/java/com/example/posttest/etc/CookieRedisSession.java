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

        return new UserSessionTot(session_id,userSession,cookie);
    }

    private Cookie getcookie(HttpServletRequest req){

        Cookie[] cookies=req.getCookies();
        if(cookies!=null) {
            Optional<Cookie> cookie = Arrays.stream(cookies).filter(x -> "JSESSIONID".equals(x.getName()))
                    .findFirst();


            if(cookie.isPresent()){

                return cookie.get();}


            return null;
        }

        return null;


    }
    private UserSession getusersession(String session_id){

        try {
            Long time = redisTemplate.getExpire(session_id);
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
            log.info("뭐가문제인가?") ;
            String csrf = jwtUtil.genjwt();
            UserSession userSession = new UserSession(memberid, csrf);
            log.info("1");
            redisTemplate.opsForValue().set(session_key,objectMapper.writeValueAsString(userSession),1800L, TimeUnit.SECONDS);
            log.info("2");

            return new String[] {session_key,csrf};

        }


        catch (Exception e){

            throw new EtcError();
        }

    }



  public Cookie delete_user_session_tot(UserSessionTot userSessionTot){


        redisTemplate.delete(userSessionTot.getSession_id());


        return userSessionTot.getCookie();

  }



  public Cookie extend_user_session_tot(UserSessionTot userSessionTot){



        redisTemplate.expire(userSessionTot.getSession_id(),1800L,TimeUnit.SECONDS);

        return userSessionTot.getCookie();

  }


  public HttpHeaders makecookieinheader(UserSessionTot userSessionTot,String key){

        Cookie cookie=null;
        if(key=="extend") {
            cookie = extend_user_session_tot(userSessionTot);
        }
        else{

            cookie=delete_user_session_tot(userSessionTot);
        }
        HttpHeaders headers=new HttpHeaders();

        ResponseCookie responseCookie=ResponseCookie.from("JSESSIONID",cookie.getValue())
                .secure(true)
                .httpOnly(true)
                .maxAge(key=="extend" ? 1800 : 0)
                .domain(".localhost")
                .sameSite("none")
                .path("/")
                .build();
        log.info("cookie:{}",cookie);
        headers.add(HttpHeaders.SET_COOKIE,responseCookie.toString());


        return headers;
    }





}


