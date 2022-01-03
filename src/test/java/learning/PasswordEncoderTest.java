package learning;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

public class PasswordEncoderTest{

    //팩토리 메소드로 passwordEnncoder 구현체 생성
    PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Test
    void encodeWithBcryptTest(){ //기본으로 bcrypt가 설정 되어있으니 포함되어있는지 인코딩 수행
        //given
        String password = "password";

        //when
        String encodedPassword = passwordEncoder.encode(password);

        //then
        assertThat(encodedPassword).contains("bcrypt");
    }

    @Test
    void matchTest(){//rawPassword 와 encodedPassword 일치여부 검사
        //given
        String password = "password";
        String encodedPassword = passwordEncoder.encode(password);

        //when
        boolean isMatch = passwordEncoder.matches(password,encodedPassword);

        //then
        assertThat(isMatch).isTrue();
    }
}