package com.example.kukyemarketclone.handler;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtHandler { //기본적으로 Base64로 인코딩된 키를 파라미터로 받게됨  = jwt.dependency를 이용할때 인코딩된 키를 인자로 넘겨줘야하기 때문

    //생성한 토큰이 어떤 타입인지(jwt)
    private String type = "Bearer ";

    /* createToken :
    * Base64로 인코딩된 key값을 받고,
    * 토큰에 저장될 데이터 subject, 만료기간 maxAgeSeconds를 초단위로 받아서 토큰을 만들어주는 작업 수행
    * */
    public String createToken(String encodedKey, String subject, long maxAgeSeconds){
        Date now = new Date();
        return type+ Jwts.builder()
                .setSubject(subject) //토큰에 저장될 데이터
                .setIssuedAt(now)  //토큰 발급일 현재시간 + 입력된 시간
                .setExpiration(new Date(now.getTime() + maxAgeSeconds * 1000L)) //Date ms 단위기 떄문에 * 1000L
                .signWith(SignatureAlgorithm.HS256, encodedKey) //HS256 알고리즘을 사용하여 서명
                .compact();
    }

    /* extractSubject : 토큰에서 subject 추출
    * subject에 memberId가 저장되기 때문에 이를 이용하여 사용자 인증
    * */
    public String extractSubject(String encodedKey, String token){
        return parse(encodedKey, token).getBody().getSubject();
    }

    // validate : 토큰 유효성 검증
    public boolean validate(String encodedKey, String token){
        try{
            parse(encodedKey,token);
            return true;

        }catch (JwtException e){
            return false;
        }
    }


    private Jws<Claims> parse(String key, String token){
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(untype(token));
    }

    //토큰 문자열에 타입 제거
    private String untype(String token){
        return token.substring(type.length());
    }


}
