package com.example.kukyemarketclone.advice;

import com.example.kukyemarketclone.dto.response.Response;
import com.example.kukyemarketclone.exception.*;
import com.example.kukyemarketclone.handler.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.kukyemarketclone.exception.ExceptionType.*;


@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionAdvice {

    private final ResponseHandler responseHandler;
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
        return getFailureResponse(EXCEPTION);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)// 403
    public Response accessDeniedException(){
        return getFailureResponse(ACCESS_DENIED_EXCEPTION);
    }

    //요청 객체의 validation 수행 시 MethodArgumentNotValidException 발생
    //추가. posts.create시 BindException 발생 -> BindException 은 MethodArgumentNotValidException보다 상위 클래스 이므로 변경
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)//400 각 검증 어노테이션 별로 지정해놨던 메세지를 응답
    public Response bindException(BindException e){
        return getFailureResponse(BIND_EXCEPTION,e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(LoginFailureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)//401 아이디, 비밀번호 오류
    public Response loginFailureException(){
        return getFailureResponse(LOGIN_FAILURE_EXCEPTION);
    }

    @ExceptionHandler(MemberEmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)//409 중복 발생
    public Response memberEmailAlreadyExistsException(MemberEmailAlreadyExistsException e){
        return getFailureResponse(MEMBER_EMAIL_ALREADY_EXISTS_EXCEPTION,e.getMessage());
    }

    @ExceptionHandler(MemberNicknameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)//409 중복 발생
    public Response MemberNicknameAlreadyExistsException(MemberNicknameAlreadyExistsException e){
        return getFailureResponse(MEMBER_NICKNAME_ALREADY_EXISTS_EXCEPTION,e.getMessage());
    }

    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)//404 요청 자원 못찾음
    public Response MemberNotFoundException(){
        return getFailureResponse(MEMBER_NOT_FOUND_EXCEPTION);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)//404 요청 자원 못찾음
    public Response RoleNotFoundException(){
        return getFailureResponse(ROLE_NOT_FOUND_EXCEPTION);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response MissingRequestHeaderException(MissingRequestHeaderException e){
        return getFailureResponse(MISSING_REQUEST_HEADER_EXCEPTION,e.getHeaderName());

    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response categoryNotFoundException(){
        return getFailureResponse(CATEGORY_NOT_FOUND_EXCEPTION);
    }

    @ExceptionHandler(CannotConvertNestedStructureException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response cannotConvertNestedStructureException(CannotConvertNestedStructureException e){
        log.info("e= {}",e.getMessage());
        return getFailureResponse(CANNOT_CONVERT_NESTED_STRUCTURE_EXCEPTION);
    }

    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response postNotFoundException(){
        return getFailureResponse(POST_NOT_FOUND_EXCEPTION);
    }

    @ExceptionHandler(UnsupportedImageFormatException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response UnsupportedImageFormatException(){
        return getFailureResponse(UNSUPPORTED_IMAGE_FORMAT_EXCEPTION);
    }

    @ExceptionHandler(FileUploadFailureException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response fileUploadFailureException(FileUploadFailureException e){
        log.info("e={}",e.getMessage());
        return getFailureResponse(FILE_UPLOAD_FAILURE_EXCEPTION);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response CommentNotFoundException(){
        return getFailureResponse(COMMENT_NOT_FOUND_EXCEPTION);
    }

    @ExceptionHandler(MessageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response MessageNotFoundException(){
        return getFailureResponse(MESSAGE_NOT_FOUND_EXCEPTION);
    }

    @ExceptionHandler(RefreshTokenFailureException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response refreshFailureException(){
        return getFailureResponse(REFRESH_TOKEN_FAILURE_EXCEPTION);
    }

    private Response getFailureResponse(ExceptionType exceptionType){
        return responseHandler.getFailureResponse(exceptionType);
    }

    private Response getFailureResponse(ExceptionType exceptionType, Object... args){
        return responseHandler.getFailureResponse(exceptionType, args);
    }

}
