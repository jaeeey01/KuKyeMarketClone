package learning;


import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OptionalTest {

    @Test   //null 이 주어질 경우 map() 호출되지 않으면 5L이 반환 = map() 에 Exception 던져지지 않음
    void doseNotInvokeOptionalInnerFunctionByOuterNullValueTest(){
        //given, when
        Long result = Optional.empty()
                .map(id -> Optional.<Long>empty().orElseThrow(RuntimeException::new))
                .orElse(5L);

        //then
        assertThat(result).isEqualTo(5L);
    }

    @Test //Optional.ofNullable(5L) null이 아닌 다른 값이 주어진다면 map 호출 됨
    void catchWhenExceptionIsThrownInOptionalInnerFunctionTest(){
        //given, when, then
        assertThatThrownBy(
                () -> Optional.ofNullable(5L)
                        .map(id -> Optional.empty().orElseThrow(RuntimeException::new))
                        .orElse(1L))
                .isInstanceOf(RuntimeException.class);
    }

}
