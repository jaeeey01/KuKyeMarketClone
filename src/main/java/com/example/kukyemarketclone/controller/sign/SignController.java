package com.example.kukyemarketclone.controller.sign;

import com.example.kukyemarketclone.controller.response.Response;
import com.example.kukyemarketclone.dto.sign.SignInRequest;
import com.example.kukyemarketclone.dto.sign.SignUpRequest;
import com.example.kukyemarketclone.service.sign.SignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.example.kukyemarketclone.controller.response.Response.success;

@RestController //객체 반환시 JSON으로 변환
@RequiredArgsConstructor
public class SignController {
    private final SignService signService;

    @PostMapping("/api/sign-up")
    @ResponseStatus(HttpStatus.CREATED)//회원가입 성공시 201 코드

    //요청 받는 JSON바디를 객체로 변환 : @RequestBody
    //Request 객체의 필드 값 검증 : @Valid
    public Response signUp(@Valid @RequestBody SignUpRequest req){
        signService.signUp(req);
        return success();
    }
    
    @PostMapping("/api/sign-in")
    @ResponseStatus(HttpStatus.OK)//정상 로그인 : 200
    public Response signIn(@Valid @RequestBody SignInRequest req){
        return success(signService.signIn(req)); // 정상 로그인시 토큰으로 응답
    }
}
