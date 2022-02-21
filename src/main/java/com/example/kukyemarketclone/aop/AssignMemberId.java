package com.example.kukyemarketclone.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface AssignMemberId {
    //컨트롤러에서 전달받은 postCreateRequest에 요청자 memberId를 주입 할 수 있도록 함
    //AOP를 이용하면 기존 코드를 건드리지 않고도, 새로운 부가기능을 추가 가능 = 컨트롤러 수정 없이 요청이 바인딩된 컨트롤러 파라미터에 memberId 주입 가능
    //memberId 주입이 필요한 요청 메소드에 어노테이션을 선언하여 지정된 요청 파라미터에 memberId 주입 기능 부여.
}
