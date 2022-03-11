package com.example.kukyemarketclone.config.security;

import org.springframework.security.access.AccessDeniedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import java.io.IOException;

public class CustomAccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    /*CustomAccessDeniedHandler : 인증은 되었어도, 사용자가 요청에 대한 접근 권한이 없을 때 작동되는 핸들러
    *  이러한 핸들러의 작동은 컨트롤러 계층에 도달 하기전에 수행 되기 떄문에, 예외가 발생한다 해도
    * 우리가 예외를 편리하기 다루기 위해 등록했던 ExceptionAdvice에서는 이 예외를 잡아 낼 수 없음 = 스프링에서 제공해주는 응답방식 이용 가능 x
    * 하지만 Response클래스를 이용하여 일관화된 응답 방식을 취하고 있고,
    * 한 곳에서 예외 사항들을 편리하게 다루기 위해 @RestControllerAdvice를 선언한 ExceptionAdvice를 사용하고 있음
    * 그래서 예외사항을 다루는 방식의 일관성을 위해 "/exception/{예외}"로 리다이렉트 시키고
    * 거기에서 이에대한 예외를 발생시켜서, ExceptionAdvice클래스에서 이 예외를 다룰 수 있도록 함
    * 이미 사용자 인증 및 인가에 대한 검사가 끝나고 예외가 발생하여 리다이렉트 되는 것이기 때문에
    * securityConfig에서 "/exception"으로 시작하는 URL에 대해 다시 검사하지 않도록 한 것
    * */

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(SC_FORBIDDEN);
    }
}
