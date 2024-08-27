package com.example.posttest.etc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Component
@Slf4j
public class JwtUtil {

    @Value("${spring.jwt.secret}")
    private  String key;

    @Value("${spring.jwt.expiration}")
    private  Long expiration;

    private  RedisTemplate<String,Object> redisTemplate;
    @Autowired
    public JwtUtil(@Qualifier("redisTemplate") RedisTemplate<String,Object> redisTemplate) {
        this.redisTemplate =redisTemplate;
    }

    public JwtToken genjwt(Long id){


        Long now=System.currentTimeMillis();


        String accesstoken= Jwts.builder()
                .claim("user_id",id)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now+expiration))
                .signWith(SignatureAlgorithm.HS256,key)
                .compact();

        String refreshtoken=Jwts
                .builder()
                .claim("user_id",id)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now+expiration+60000))
                .signWith(SignatureAlgorithm.HS256,key)
                .compact();

       // redisTemplate.opsForValue().set(accesstoken,refreshtoken,300,TimeUnit.SECONDS);
        return JwtToken.builder()
                .accesstoken(accesstoken)
                .refreshtoken(refreshtoken)
                .grantType("Bearer")
                .build();


    }


    public String regenaccesstoken(Long id){
        //String claim=auth.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        Long now=System.currentTimeMillis();
        String accesstoken= Jwts.builder()
                .claim("user_id",id)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now+30000))
                .signWith(SignatureAlgorithm.HS256,key)
                .compact();



        return accesstoken;



    }




    public List<Object> getdatafromtoken(String token){
        Claims claims=getclaims(token);
        log.info("datatype:{}",claims.get("user_id").getClass());
        Integer x=(Integer)claims.get("user_id");
        //String auth=(String)claims.get("auth");
        String username=claims.getSubject();
        List<Object> datalist=new ArrayList<>();
        datalist.add(x.longValue());
        datalist.add(username);
        return datalist;
    }



    public Long getidfromtoken_decode_payload(String token) throws JsonProcessingException {
        String[] token_arr=token.split("\\.");

        String payload=token_arr[1];
        String decode_payload=new String(Base64.getDecoder().decode(payload));

        ObjectMapper objectMapper=new ObjectMapper();
        Map<String, Object> map =objectMapper.readValue(decode_payload,Map.class);

        return ((Integer)map.get("user_id")).longValue();
    }

    public Long getidfromtoken(String token){
        Claims claims=getclaims(token);
        Integer x=(Integer)claims.get("user_id");
        return x.longValue();
    }


    public boolean validatetoken(String token)throws MalformedJwtException, ExpiredJwtException,UnsupportedJwtException,IllegalArgumentException{



        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            // 로그를 추가하여 예외가 캐치되는지 확인합니다.

            throw e; // 예외를 던져서 상위 메서드에서 처리하도록 합니다.
        }

    }


    public Claims getclaims(String token)throws SecurityException, MalformedJwtException, ExpiredJwtException,UnsupportedJwtException,IllegalArgumentException{

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

    }
}
