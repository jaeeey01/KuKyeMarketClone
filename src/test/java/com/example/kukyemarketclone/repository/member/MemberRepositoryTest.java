package com.example.kukyemarketclone.repository.member;

import com.example.kukyemarketclone.config.QuerydslConfig;
import com.example.kukyemarketclone.entity.member.Member;
import com.example.kukyemarketclone.entity.member.MemberRole;
import com.example.kukyemarketclone.entity.member.Role;
import com.example.kukyemarketclone.entity.member.RoleType;
import com.example.kukyemarketclone.exception.MemberNotFoundException;
import com.example.kukyemarketclone.repository.role.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.kukyemarketclone.factory.entity.MemberFactory.createMember;
import static com.example.kukyemarketclone.factory.entity.MemberFactory.createMemberWithRoles;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest //JPA관련 TEST만 할 것 : JPA관련 설정이나 Repository들만 스프링빈으로 등록, @Autowired로 주입받음
@Import(QuerydslConfig.class)
class MemberRepositoryTest {

    /*
    *jpa를 이용하여 쿼리 수행시, 상황에 따라 즉시 쿼리가 수행되는것이 아닌 필요한 시점에 쿼리 실행
    * 영속성 컨텍스트라는 곳에 엔티티를 캐시해두기 때문에 엔티티를 조회 & 저장시 DB에서 가져오는것이 아닌 캐시해둔 엔티티를 꺼내옴
    * DB와 연동하여 Repository를 테스트하기때문에 EntityManager를 주입받아서 쿼리를 즉시 날리거나 캐시를 비우는 용도로 사용
    * */

    @Autowired MemberRepository memberRepository;
    @Autowired RoleRepository roleRepository;
    @PersistenceContext EntityManager em;

    @Test
    void createAndReadTest(){
        //given : 테스트에 필요한 데이터 또는 상황
        Member member = createMember();

        //when : 테스트 수행
        memberRepository.save(member);
        clear();

        //then : 테스트의 결과 검증
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        assertThat(foundMember.getId()).isEqualTo(member.getId());

    }

    @Test
    void memberDateTest(){  //@MappedSuperClass로 선언하여 상속받은 EntityDate 클래스의 필드들이 자동 추가 되었는지 확인
        //given
        Member member = createMember();

        //when
        memberRepository.save(member);
        clear();

        //then
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        assertThat(foundMember.getCreatedAt()).isNotNull();
        assertThat(foundMember.getModifiedAt()).isNotNull();
        assertThat(foundMember.getCreatedAt()).isEqualTo(foundMember.getModifiedAt());
    }

    @Test
    void updateTest(){
        //given
        String updatedNickname = "updated";
        Member member = memberRepository.save(createMember());
        clear();

        //when
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        foundMember.updateNickname(updatedNickname);
        clear();

        //then
        Member updatedMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        assertThat(updatedMember.getNickname()).isEqualTo(updatedNickname);
    }

    @Test
    void deleteTest(){ //객체가 없을때 예외발생 -> 어떤 예외발생인지 테스트
        //given
        Member member = memberRepository.save(createMember());
        clear();

        //when
        memberRepository.delete(member);
        clear();

        //then
        assertThatThrownBy(() -> memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new))
                .isInstanceOf(MemberNotFoundException.class);
        //assertThatThrownBy(() -> {수행할 테스트} ).isInstanceOf(던져지는예외.class)
    }

    @Test
    void findByEmailTest(){
        //given
        Member member = memberRepository.save(createMember());
        clear();

        //when
        Member foundMember = memberRepository.findByEmail(member.getEmail()).orElseThrow(MemberNotFoundException::new);

        //then
        assertThat(foundMember.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    void findNicknameTest(){
        //given
        Member member = memberRepository.save(createMember());
        clear();

        //when
        Member foundMember = memberRepository.findByNickname(member.getNickname()).orElseThrow(MemberNotFoundException::new);

        //then
        assertThat(foundMember.getNickname()).isEqualTo(member.getNickname());
    }


    @Test
    void uniqueEmailTest(){ // unique 제약조건 테스트 중복된 데이터 입력시 DataIntegrityViolationException 발생 해야함
        //given
        Member member = memberRepository.save(createMember("email1","password1","username1","nickname1"));
        clear();

        //when, then
        assertThatThrownBy(() -> memberRepository.save(createMember(member.getEmail(),"password2","username2","nickname2")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void uniqueNicknameTest(){
        //given
        Member member = memberRepository.save(createMember("email1","password1","username1","nickname1"));
        clear();

        //when,then
        assertThatThrownBy( () -> memberRepository.save(createMember("email2","password2","username2",member.getNickname())))
                .isInstanceOf(DataIntegrityViolationException.class);
    }


    @Test
    void existsByEmailTest(){
        //given
        Member member = memberRepository.save(createMember());
        clear();

        //when,then
        assertThat(memberRepository.existsByEmail(member.getEmail())).isTrue();
        assertThat(memberRepository.existsByEmail(member.getEmail() + "test")).isFalse();

    }

    @Test
    void existsByNicknameTest(){
        //given
        Member member = memberRepository.save(createMember());
        clear();

        //when, then
        assertThat(memberRepository.existsByNickname(member.getNickname())).isTrue();
        assertThat(memberRepository.existsByNickname(member.getNickname()+ "Test")).isFalse();
    }

    @Test
    void memberRoleCascadePersistTest(){ //Member 엔티티가 @OneToMany관계를 가진 MemberRole이 cascade(연달아서)하게 persist(저장)되는지 검증
        //given
        List<RoleType> roleTypes = List.of(RoleType.ROLE_NORMAL,RoleType.ROLE_SPECIAL_BUYER,RoleType.ROLE_ADMIN);
        List<Role> roles = roleTypes.stream().map(roleType -> new Role(roleType)).collect(Collectors.toList());
        roleRepository.saveAll(roles);
        clear();

        Member member = memberRepository.save(createMemberWithRoles(roleRepository.findAll()));
        clear();

        //when
        Member foundMember = memberRepository.findById(member.getId()).orElseThrow(MemberNotFoundException::new);
        Set<MemberRole> memberRoles = foundMember.getRoles();

        //then
        assertThat(memberRoles.size()).isEqualTo(roles.size());
    }

    @Test
    void memberRoleCascadeDeleteTest(){ //Member 제거시 MemberRole 또한 함께 제거되는지 테스트
        //given
        List<RoleType> roleTypes = List.of(RoleType.ROLE_NORMAL, RoleType.ROLE_SPECIAL_BUYER,RoleType.ROLE_ADMIN);
        List<Role> roles = roleTypes.stream().map(roleType -> new Role(roleType)).collect(Collectors.toList());
        clear();

        Member member = memberRepository.save(createMemberWithRoles(roleRepository.findAll()));
        clear();

        //when
        memberRepository.deleteById(member.getId());
        clear();

        //then
        List<MemberRole> rs = em.createQuery("select mr from MemberRole mr",MemberRole.class).getResultList();
        assertThat(rs.size()).isZero();


    }


    private void clear(){
        em.flush(); // 쿼리를 즉시 수행
        em.clear();// 캐쉬를 비움
    }


}