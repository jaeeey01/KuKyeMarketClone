package com.example.kukyemarketclone.advice;

import com.example.kukyemarketclone.dto.response.Response;
import com.example.kukyemarketclone.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionAdvice {

    private final MessageSource messageSource;
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
        return getFailureResponse("exception.code","exception.msg");
    }

    @ExceptionHandler(AuthenticationEntryPointException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)// 401
    public Response authenticationEntryPoint(){
        return getFailureResponse("authenticationEntryPoint.code","authenticationEntryPoint.msg");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)// 403
    public Response accessDeniedException(){
        return getFailureResponse("accessDeniedException.code","accessDeniedException.msg");
    }

    //요청 객체의 validation 수행 시 MethodArgumentNotValidException 발생
    //추가. posts.create시 BindException 발생 -> BindException 은 MethodArgumentNotValidException보다 상위 클래스 이므로 변경
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)//400 각 검증 어노테이션 별로 지정해놨던 메세지를 응답
    public Response bindException(BindException e){
        return getFailureResponse("bindException.code","bindException.msg",e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(LoginFailureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)//401 아이디, 비밀번호 오류
    public Response loginFailureException(){
        return getFailureResponse("loginFailureException.code","loginFailureException.msg");
    }

    @ExceptionHandler(MemberEmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)//409 중복 발생
    public Response memberEmailAlreadyExistsException(MemberEmailAlreadyExistsException e){
        return getFailureResponse("memberEmailAlreadyExistsException.code","memberEmailAlreadyExistsException.msg");
    }

    @ExceptionHandler(MemberNicknameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)//409 중복 발생
    public Response MemberNicknameAlreadyExistsException(MemberNicknameAlreadyExistsException e){
        return getFailureResponse("memberNicknameAlreadyExistsException.code","memberNicknameAlreadyExistsException.msg",e.getMessage());
    }

    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)//404 요청 자원 못찾음
    public Response MemberNotFoundException(){
        return getFailureResponse("memberNotFoundException.code","memberNotFoundException.msg");
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)//404 요청 자원 못찾음
    public Response RoleNotFoundException(){
        return getFailureResponse("roleNotFoundException.code","roleNotFoundException.msg");
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response MissingRequestHeaderException(MissingRequestHeaderException e){
        return getFailureResponse("missingRequestHeaderException.code","missingRequestHeaderException.msg");

    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response categoryNotFoundException(){
        return getFailureResponse("categoryNotFoundException.code","categoryNotFoundException.msg");
    }

    @ExceptionHandler(CannotConvertNestedStructureException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response cannotConvertNestedStructureException(CannotConvertNestedStructureException e){
        log.info("e= {}",e.getMessage());
        return getFailureResponse("cannotConvertNestedStructureException.code","cannotConvertNestedStructureException.msg");
    }

    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response postNotFoundException(){
        return getFailureResponse("postNotFoundException.code","postNotFoundException.msg");
    }

    @ExceptionHandler(UnsupportedImageFormatException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response UnsupportedImageFormatException(){
        return getFailureResponse("unsupportedImageFormatException.code","unsupportedImageFormatException.msg");
    }

    @ExceptionHandler(FileUploadFailureException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response fileUploadFailureException(FileUploadFailureException e){
        log.info("e={}",e.getMessage());
        return getFailureResponse("fileUploadFailureException.code","fileUploadFailureException.msg");
    }

    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response CommentNotFoundException(){
        return getFailureResponse("commentNotFoundException.code","commentNotFoundException.msg");
    }

    @ExceptionHandler(MessageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response MessageNotFoundException(){
        return getFailureResponse("messageNotFoundException.code","messageNotFoundException.msg");
    }

    private Response getFailureResponse(String codeKey, String messageKey){
        log.info("code = {}, msg ={}", getCode(codeKey), getMessage(messageKey,null));
        return Response.failure(getCode(codeKey), getMessage(messageKey,null));
    }

    private Response getFailureResponse(String codeKey, String messageKey, Object... args){
        return Response.failure(getCode(codeKey), getMessage(messageKey, args));
    }

    private Integer getCode(String key){
        return Integer.valueOf(messageSource.getMessage(key,null,null));
    }

    private String getMessage(String key, Object... args){
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

}
