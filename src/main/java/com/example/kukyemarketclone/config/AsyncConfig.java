package com.example.kukyemarketclone.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync    //Async 활성화
@Configuration
@Slf4j
@Profile("!test")   //이벤트에 대한 테스트 수행시에는 반드시 비동기로 처리할 필요 없음
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor(){//스레드 풀 지정, 미지정시 비효율적인 디폴트 방식 사용 하게 됨
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(3);//초기 스래드 개수
        taskExecutor.setMaxPoolSize(10);//CorePoolSize가 모두 사용 중일 경우 만들어지는 최대 스래드 개수
        taskExecutor.setQueueCapacity(50); //MaxPoolSize가 모두 사용중일 경우 대기하는 큐의 크기
        taskExecutor.setThreadNamePrefix("async-thread-");//스래드 접두어
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler(){
        //비동기메소드에서 발생하는 예외는 @RestControllerAdvice에서 잡아낼수 없음
        //비동기 메소드에서도 단일화된 예외 관리 목적
        return (ex, method, params) -> log.info("exception occurred in {} {} : {}",method.getName(),params,ex.getMessage() );
    }
}
