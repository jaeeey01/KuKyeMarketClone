package com.example.kukyemarketclone;

import com.example.kukyemarketclone.entity.category.Category;
import com.example.kukyemarketclone.entity.member.Member;
import com.example.kukyemarketclone.entity.member.Role;
import com.example.kukyemarketclone.entity.member.RoleType;
import com.example.kukyemarketclone.entity.post.Post;
import com.example.kukyemarketclone.exception.RoleNotFoundException;
import com.example.kukyemarketclone.repository.category.CategoryRepository;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import com.example.kukyemarketclone.repository.post.PostRepository;
import com.example.kukyemarketclone.repository.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    /*@PostConstruct 메소드에 지정하면, 빈의 생성과 의존성 주입이 끝난 뒤에 수행할 초기화 코드를 지정할 수 있음
    * @Transactional과 같은 AOP가 적용 안됨. @Transactional과 같은 AOP는 빈후처리기에 의해 처리되는데,
    * @PostConstruct는 이러한 모든 후처리가 완료되었는지를 확인 할 수 없음
    *  */
    /*@PostConstruct*/

    /*@EventListener, @Transactional 으로 변경이유
    *  @PostConstruct로 테스트시
    *  detached entity passed to persist: example.kukyemarketclone.entity.member.Role; 에러 발생
    *  = persist를 위해 전달 된, 분리된 엔티티 Role이 문제
    *  관련 내용 해결법 : CascadeType.PERSIST 제거하면 해결
    *  하지만 이렇게 할경우 Member를 저장시 MemberRole이 cascade하지 않게 됨(cascade 저장 테스트도 실패)
    *  = ApplicationReadyEvent가 발생시 initDB 메소드 호출
    *  = 모든 준비가 완료되었을 때 발생하는 이벤트로 , @Transactional 이용한 AOP 적용 가능
    * */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initDB(){
        initRole();
        initTestAdmin();
        initTestMember();
        initCategory();
        initPost();
        log.info("initialize database");
    }

    private void initRole(){
        roleRepository.saveAll(
                List.of(RoleType.values()).stream().map(roleType -> new Role(roleType)).collect(Collectors.toList())
        );
    }

    private void initTestAdmin(){
        memberRepository.save(
                new Member("admin@admin.com", passwordEncoder.encode("123456a!"), "admin","admin",
                        List.of(roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new),
                                roleRepository.findByRoleType(RoleType.ROLE_ADMIN).orElseThrow(RoleNotFoundException::new)))
        );
    }

    private void initTestMember(){
        memberRepository.saveAll(
          List.of(
                  new Member("member1@member.com", passwordEncoder.encode("123456a!"),"member1","member1",
                          List.of(roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new))),
                  new Member("member2@member.com",passwordEncoder.encode("123456a!"),"member2","member2",
                          List.of(roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new))))
        );
    }

    private void initCategory() {
        Category c1 = categoryRepository.save(new Category("category1", null));
        Category c2 = categoryRepository.save(new Category("category2", c1));
        Category c3 = categoryRepository.save(new Category("category3", c1));
        Category c4 = categoryRepository.save(new Category("category4", c2));
        Category c5 = categoryRepository.save(new Category("category5", c2));
        Category c6 = categoryRepository.save(new Category("category6", c4));
        Category c7 = categoryRepository.save(new Category("category7", c3));
        Category c8 = categoryRepository.save(new Category("category8", null));
    }

    private void initPost(){
        Member member = memberRepository.findAll().get(0);
        Category category = categoryRepository.findAll().get(0);
        IntStream.range(0,100)
                .forEach(i -> postRepository.save(
                        new Post("title" + i, "content" + i, Long.valueOf(i), member, category, List.of())
                ));
    }
}
