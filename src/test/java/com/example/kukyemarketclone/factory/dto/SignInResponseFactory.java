package com.example.kukyemarketclone.factory.dto;

import com.example.kukyemarketclone.dto.sign.SignInResponse;

public class SignInResponseFactory {

    public static SignInResponse createSignInResponse(String accessToken, String refreshToken){
        return new SignInResponse(accessToken, refreshToken);
    }
}
