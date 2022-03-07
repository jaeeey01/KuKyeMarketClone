package com.example.kukyemarketclone.config.token;

import com.example.kukyemarketclone.handler.JwtHandler;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TokenHelper {

    private final JwtHandler jwtHandler;
    private final String key;
    private final long maxAgeSeconds;
    
    private static final String SEP = ",";
    private static final String ROLE_TYPES = "ROLE_TYPES";
    private static final String MEMBER_ID = "MEMBER_ID";

    
    //변경 : 토큰생성시 privateClaims 인자 전달 받음,
    // 범용적으로 사용될 수 있던 JwtHandler와 달리 인증방식에서 필요한 정보만 privateClaims로 전달 받음
    // 여러개의 권한은 하나의 스트림으로 저장 
    public String createToken(PrivateClaims privateClaims){
        return jwtHandler.createToken(
                key,
                Map.of(MEMBER_ID,privateClaims.getMemberId(),ROLE_TYPES,privateClaims.getRoleTypes().stream().collect(Collectors.joining(SEP))),
                maxAgeSeconds
        );
    }

    //jwtHandler와 마찬가지로 유효하지 않은 토큰이라면 Optional반환
    public Optional<PrivateClaims> parse(String token){
        return jwtHandler.parse(key,token).map(this::convert);
    }
    
    private PrivateClaims convert(Claims claims){
        return new PrivateClaims(
                claims.get(MEMBER_ID,String.class),
                Arrays.asList(claims.get(ROLE_TYPES,String.class).split(SEP))
        );
    }
    
    
    @Getter
    @AllArgsConstructor
    public static class PrivateClaims{//토큰에 저장될 비공개클레임
        private String memberId;
        private List<String> roleTypes;
    }

}
