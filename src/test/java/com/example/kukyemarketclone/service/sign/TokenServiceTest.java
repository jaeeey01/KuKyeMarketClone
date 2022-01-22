package com.example.kukyemarketclone.service.sign;

import com.example.kukyemarketclone.handler.JwtHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @InjectMocks    //선언시, 의존성을 가지고 있는 객체들을 가짜로 만들어서 주입받을 수 있도록 함
    TokenService tokenService;
    @Mock           //선언시, 객체들을 가짜로 만들어서 @InjectMocks로 지정된 객체에 주입
    JwtHandler jwtHandler;

    /*
    * TokenService는 @Value 이용하여 설정파일에서 값을 읽어와야함
    * 하지만 단위테스트만 수행 할 것 = 해당 값을 읽어 올 수 없음 = nullpoint
    *  방법 1 : tokenService에 setter 메서드 생성
    *  -> 테스트 수행전 가짜 TokenService에 값 주입 -> TokenService에 굳이 setter 필요없음
    *  방법 2 : ReflectionTestUtils 이용
    *  -> setter 메소드 없이 리플렉션을 이용하여 어떠한 객체의 필드 값에 임의값 주입 가능
     * */
    @BeforeEach // 테스트 진행 하기 전 수행
    void beforeEach(){
        //ReflectionTestUtils.setField(값을 주입해줄 객체,주입할 필드명 ,주입할 값);
        ReflectionTestUtils.setField(tokenService,"accessTokenMaxAgeSeconds",10L);
        ReflectionTestUtils.setField(tokenService,"refreshTokenMaxAgeSeconds",10L);
        ReflectionTestUtils.setField(tokenService,"accessKey","accessKey");
        ReflectionTestUtils.setField(tokenService,"refreshKey","refreshKey");
    }

    @Test
    void createAccessTokenTest() {
        //given
        //given() : 의존하는 가짜 객체의 행위가 반환해야할 데이터를 미리 준비하여 주입
        //willReturn() 이 객체의 행위가 반환해야할 데이터를 준비해서 지정
        // = 결과값 검증
        given(jwtHandler.createToken(anyString(),anyString(),anyLong())).willReturn("access");

        //when
        String token = tokenService.createAccessToken("subject");

        //then
        //verify() : 그 가짜 객체가 수행한 행위 검증
        assertThat(token).isEqualTo("access");
        verify(jwtHandler).createToken(anyString(),anyString(),anyLong());

    }

    @Test
    void createRefreshTokenTest() {
        //given
        given(jwtHandler.createToken(anyString(),anyString(),anyLong())).willReturn("refresh");

        //when
        String token = tokenService.createRefreshToken("subject");

        //then
        assertThat(token).isEqualTo("refresh");
        verify(jwtHandler).createToken(anyString(),anyString(),anyLong());
    }
}