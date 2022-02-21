package com.example.kukyemarketclone.advice;

import com.example.kukyemarketclone.dto.response.Response;
import com.example.kukyemarketclone.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    /*
    * @ExceptionHandler : 예외 클래스 지정시, 실행중에 지정한 예외 발생 -> 해당 메서드 실행
    * @ResponseStatus :  각 예외마다 상태코드 지정
    * @RestControllerAdvice로 지정했기때문에 @ResponseBody가 포함되어 있음
    * 이를 이용하여 Response.failure에 오류코드와 오류 메세지를 같이 응답
    *
    */

    //의도하지 않은 예외 발생시 로그 남기고 응답 = 다른 exceptionHandler에서 잡지 못한 예외는 여기로옴
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response exception(Exception e){
        log.info("e={}", e.getMessage());
        return Response.failure(-1000,"오류가 발생하였습니다");
    }

    @ExceptionHandler(AuthenticationEntryPointException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)// 401
    public Response authenticationEntryPoint(){
        return Response.failure(-1001,"인증되지 않은 사용자 입니다");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)// 403
    public Response accessDeniedException(){
        return Response.failure(-1002,"접근이 거부되었습니다");
    }

    //요청 객체의 validation 수행 시 MethodArgumentNotValidException 발생
    //추가. posts.create시 BindException 발생 -> BindException 은 MethodArgumentNotValidException보다 상위 클래스 이므로 변경
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)//400 각 검증 어노테이션 별로 지정해놨던 메세지를 응답
    public Response bindException(BindException e){
        return Response.failure(-1003, e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(LoginFailureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)//401 아이디, 비밀번호 오류
    public Response loginFailureException(){
        return Response.failure(-1004,"로그인에 실패하였습니다");
    }

    @ExceptionHandler(MemberEmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)//409 중복 발생
    public Response memberEmailAlreadyExistsException(MemberEmailAlreadyExistsException e){
        return Response.failure(-1005,e.getMessage() + "은 중복된 이메일 입니다");
    }

    @ExceptionHandler(MemberNicknameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)//409 중복 발생
    public Response MemberNicknameAlreadyExistsException(MemberNicknameAlreadyExistsException e){
        return Response.failure(-1006,e.getMessage() + "은 중복된 닉네임 입니다");
    }

    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)//404 요청 자원 못찾음
    public Response MemberNotFoundException(){
        return Response.failure(-1007,"요청한 회원을 찾을 수 없습니다");
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)//404 요청 자원 못찾음
    public Response RoleNotFoundException(){
        return Response.failure(-1008,"요청한 권한 등급을 찾을 수 없습니다");
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response MissingRequestHeaderException(MissingRequestHeaderException e){
        return Response.failure(-1009,e.getHeaderName() + "요청 헤더가 누락되었습니다");
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response categoryNotFoundException(){
        return Response.failure(-1010,"존재하지 않는 카테고리 입니다");
    }

    @ExceptionHandler(CannotConvertNestedStructureException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response cannotConvertNestedStructureException(CannotConvertNestedStructureException e){
        log.info("e= {}",e.getMessage());
        return Response.failure(-1011,"중첩 구조 변환에 실패하였습니다.");
    }

    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response postNotFoundException(){
        return Response.failure(-1012,"존재하지 않는 게시글 입니다");
    }

    @ExceptionHandler(UnsupportedImageFormatException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response UnsupportedImageFormatException(){
        return Response.failure(-1013,"지원하지 않는 이미지 형식입니다");
    }

    @ExceptionHandler(FileUploadFailureException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response fileUploadFailureException(FileUploadFailureException e){
        log.info("e={}",e.getMessage());
        return Response.failure(-1014,"파일 업로드에 실패하였습니다");
    }


}
