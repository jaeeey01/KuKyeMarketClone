package com.example.kukyemarketclone;

import com.example.kukyemarketclone.entity.member.Role;
import com.example.kukyemarketclone.entity.member.RoleType;
import com.example.kukyemarketclone.repository.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("local")
public class InitDB {

    /* 현재 application.yml에 활성 profile은 local로 설정
    * 이 클래스는 활성 profile이 local 일 때만 빈으로 등록
    * @PostConstruct를 메소드로 지정시, 빈의 생성과 의존성 주입이 끝난 뒤에 수행할 초기화 코드 지정할 수 있음
    * RoleType에 정의했던 권한들을 데이터베이스에 저장
    * */



    private final RoleRepository roleRepository;

    /*@PostConstruct 메소드에 지정하면, 빈의 생성과 의존성 주입이 끝난 뒤에 수행할 초기화 코드를 지정할 수 있음
    * @Transactional과 같은 AOP가 적용 안됨. @Transactional과 같은 AOP는 빈후처리기에 의해 처리되는데,
    * @PostConstruct는 이러한 모든 후처리가 완료되었는지를 확인 할 수 없음
    *  */
    @PostConstruct
    public void initDB(){
        log.info("initialize database");
        initRole();
    }

    private void initRole(){
        roleRepository.saveAll(
                List.of(RoleType.values()).stream().map(roleType -> new Role(roleType)).collect(Collectors.toList())
        );
    }

}
