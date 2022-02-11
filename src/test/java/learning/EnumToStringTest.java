package learning;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EnumToStringTest {//Enum타입을 String으로 변환 테스트

    public enum TestEnum{
        TEST1, TEST2
    }

    @Test
    void enumToStringTest(){ //Enum타입을 toString으로 String 변환 가능여부 확인 테스트
        assertThat(TestEnum.TEST1.toString()).isEqualTo("TEST1");
        assertThat(TestEnum.TEST2.toString()).isEqualTo("TEST2");
    }

}
