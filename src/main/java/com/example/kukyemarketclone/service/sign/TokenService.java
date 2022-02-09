package com.example.kukyemarketclone.service.sign;

import com.example.kukyemarketclone.handler.JwtHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtHandler jwtHandler;

    //@Value : 설정파일(application.yml)에 작성된 내용 가져옴
    @Value("${jwt.max-age.access}")
    private long accessTokenMaxAgeSeconds;

    @Value("${jwt.max-age.refresh}")
    private long refreshTokenMaxAgeSeconds;

    @Value("${jwt.key.access}")
    private String accessKey;

    @Value("${jwt.key.refresh}")
    private String refreshKey;

    public String createAccessToken(String subject){
        return jwtHandler.createToken(accessKey,subject,accessTokenMaxAgeSeconds);
    }

    public String createRefreshToken(String subject){
        return jwtHandler.createToken(refreshKey,subject,refreshTokenMaxAgeSeconds);

    }
}