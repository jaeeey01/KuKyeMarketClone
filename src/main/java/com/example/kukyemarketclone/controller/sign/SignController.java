package com.example.kukyemarketclone.controller.sign;

import com.example.kukyemarketclone.dto.response.Response;
import com.example.kukyemarketclone.dto.sign.SignInRequest;
import com.example.kukyemarketclone.dto.sign.SignUpRequest;
import com.example.kukyemarketclone.service.sign.SignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

import static com.example.kukyemarketclone.dto.response.Response.success;

@Api(value = "sign Controller", tags = "Sign")
@RestController //객체 반환시 JSON으로 변환
@RequiredArgsConstructor
public class SignController {
    private final SignService signService;


    @ApiOperation(value = "회원가입", notes = "회원가입을 한다")
    @PostMapping("/api/sign-up")
    @ResponseStatus(HttpStatus.CREATED)//회원가입 성공시 201 코드
    public Response signUp(@Valid @RequestBody SignUpRequest req){
        //요청 받는 JSON 바디를 객체로 변환 : @RequestBody
        //Request 객체의 필드 값 검증 : @Valid

        signService.signUp(req);
        return success();
    }

    @ApiOperation(value = "로그인",notes = "로그인을 한다")
    @PostMapping("/api/sign-in")
    @ResponseStatus(HttpStatus.OK)//정상 로그인 : 200
    public Response signIn(@Valid @RequestBody SignInRequest req){
        return success(signService.signIn(req)); // 정상 로그인시 토큰으로 응답
    }

    @ApiOperation(value = "토큰 재발급", notes = "리프레시 토큰으로 새로운 액세스 토큰을 발급 받는다")
    @PostMapping("/api/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    public Response refreshToken( @ApiIgnore @RequestHeader(value = "Authorization")String refreshToken){
        //@RequestHeader 헤더값 전달되지 않았을 때 예외발생 = MissingRequestHeaderException
        return success(signService.refreshToken(refreshToken));
    }

}
