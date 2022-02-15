package com.example.kukyemarketclone.factory.dto;

import com.example.kukyemarketclone.dto.sign.RefreshTokenResponse;

public class RefreshTokenResponseFactory {
    public static RefreshTokenResponse createRefreshTokenResponse(String accessToken){
        return new RefreshTokenResponse(accessToken);
    }
}
