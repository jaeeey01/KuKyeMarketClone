package com.example.kukyemarketclone.factory.dto;

import com.example.kukyemarketclone.dto.sign.SignInRequest;

public class SignInRequestFactory {

    //정상적인 요청 객체 생성 팩토리 메소드
    public static SignInRequest createSignInRequest() {
        return new SignInRequest("email@email.com","123456a!");
    }


    public static SignInRequest createSignInRequest(String email, String password){
        return new SignInRequest(email,password);
    }

    // 전달받은 이메일 외에 정상 요청 객체 생성 팩토리 메소드
    public static SignInRequest createSignInRequestWithEmail(String email) {
        return new SignInRequest(email,"123456a!");
    }


    //전달받은 패스워드 외에 정상 요청 객체 생성 팩토리 메소드
    public static SignInRequest createSignInRequestWithPassword(String password) {
        return new SignInRequest("email@email.com",password);
    }





}
