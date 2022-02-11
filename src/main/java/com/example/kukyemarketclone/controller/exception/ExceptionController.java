package com.example.kukyemarketclone.controller.exception;

import com.example.kukyemarketclone.exception.AccessDeniedException;
import com.example.kukyemarketclone.exception.AuthenticationEntryPointException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExceptionController {//아래 예외에 대한 리다이렉트를 위함

    @GetMapping("/exception/entry-point")
    public void entryPoint(){
        throw new AuthenticationEntryPointException();
    }

    @GetMapping("/exception/access-denied")
    public void accessDenied(){
        throw new AccessDeniedException();
    }

}
