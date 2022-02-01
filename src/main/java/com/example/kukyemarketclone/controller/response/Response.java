package com.example.kukyemarketclone.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)//null 값을 가지는 필드는 JSON 응답에 미포함
@AllArgsConstructor(access = AccessLevel.PRIVATE)
//스태틱 팩토리 메소드를 이용하여 인스턴트 생성 = 생성자 접근 제어레벨 private
@Getter//응답 객체를 JSON으로 변환시에 필요
public class Response {
    private boolean success;
    private int code;
    private Result result;

    /*Response 객체는 요청 성공여부와 응답 코드, 응답 데이터를 가지고 있음
    * 요청성공 : 응답코드 = 0
    * 요청 실패 : 특정한 응답 코드 = 실패원인 식별
    * */

    //요청 성공, 응답 해야할 별다른 데이터가 없을 경우
    public static Response success(){
        return new Response(true,0,null);
    }

    //요청 성공, 응답 데이터 반환
    public static<T> Response success(T data){
        return new Response(true, 0 ,new Success<>(data));
    }

    //요청 실패, 실패 메세지 반환
    public static Response failure(int code, String msg){
        return new Response(false,code, new Failure(msg));
    }
    
}
