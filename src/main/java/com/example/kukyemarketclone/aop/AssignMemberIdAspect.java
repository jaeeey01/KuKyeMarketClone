package com.example.kukyemarketclone.aop;

import com.example.kukyemarketclone.config.security.guard.AuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

@Aspect
@Component//스프링 컨테이너 등록
@RequiredArgsConstructor
@Slf4j
public class AssignMemberIdAspect {

    private final AuthHelper authHelper;

    //부가기능 수행되는 지점 지정  @Before 이요 메소드 호출전 수행
    //@AssignMemberId 가 적용된 메소드들은 본래의 메소드 수행 직전에 assignMemberId 메소드 호출됨
    @Before("@annotation(com.example.kukyemarketclone.aop.AssignMemberId)")
    public void assignMemberId(JoinPoint joinPoint){//파라미터로 전닯다은 JoinPoint를 이용하여 호출되어야할 본래의 메소드에 대한 정보 가져옴
        Arrays.stream(joinPoint.getArgs())// joinPoint.getArgs()를 이용하여 전달되는 인자들을 확인하고
                .forEach(arg -> getMethod(arg.getClass(),"setMemberId")//setMemberId로 정의된 타입이 있다면 memberId를 주입
                        .ifPresent(setMemberId -> invokeMethod(arg, setMemberId, authHelper.extactMemberId())));
    }

    /** getMethod, invokeMethod
     * 리플렉션 API에서 발생하는 checked Exception에 대한 처리를 도와줌
     * */

    private Optional<Method> getMethod(Class<?> clazz, String methodName ){
        try{
            return Optional.of(clazz.getMethod(methodName,Long.class));
        }catch (NoSuchMethodException e){
            return Optional.empty();
        }
    }

    private void invokeMethod(Object obj, Method method, Object... args){
        try{
            method.invoke(obj, args);
        }catch (ReflectiveOperationException e){
            throw new RuntimeException(e);
        }
    }
}
