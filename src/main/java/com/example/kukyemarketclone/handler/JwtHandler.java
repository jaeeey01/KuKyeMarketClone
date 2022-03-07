package com.example.kukyemarketclone.handler;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtHandler { //기본적으로 Base64로 인코딩된 키를 파라미터로 받게됨  = jwt.dependency를 이용할때 인코딩된 키를 인자로 넘겨줘야하기 때문

    //생성한 토큰이 어떤 타입인지(jwt)
    private String type = "Bearer ";

    /* createToken :
    * Base64로 인코딩된 key값을 받고,
    * 토큰에 저장될 데이터 subject, 만료기간 maxAgeSeconds를 초단위로 받아서 토큰을 만들어주는 작업 수행
    * */

    //변경 : subject 대신 비공개 클레임을 전달 받아서 토큰 생성
    // * 비공개 클레임 : 직접정의하여 사용하는 클레임
    public String createToken(String key, Map<String, Object> privateClaims, long maxAgeSeconds){
        Date now = new Date();
        return type+ Jwts.builder()
                .addClaims(privateClaims)
                .addClaims(Map.of(Claims.ISSUED_AT,now, Claims.EXPIRATION, new Date(now.getTime() + maxAgeSeconds * 1000L)))
                .signWith(SignatureAlgorithm.HS256,key.getBytes())//인코딩 된 key를 입력받던 방식 -> 바이트 배열을 SigningKey로 사용
                .compact();
    }

    //변경 : validate메소드로 하던 검증은 parse 메소드에 통합, 유효하지 않은 토큰이라면 비어있는 Optional 반환
    public Optional<Claims> parse(String key, String token){
        try{
            return Optional.of(Jwts.parser().setSigningKey(key.getBytes()).parseClaimsJws(untype(token)).getBody());
        }catch (JwtException e){
            return Optional.empty();
        }
    }

    private String untype(String token){
        return token.substring(type.length());
    }

}
