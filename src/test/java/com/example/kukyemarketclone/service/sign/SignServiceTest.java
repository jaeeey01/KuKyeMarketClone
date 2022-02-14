package com.example.kukyemarketclone.service.sign;

import com.example.kukyemarketclone.dto.sign.SignInRequest;
import com.example.kukyemarketclone.dto.sign.SignInResponse;
import com.example.kukyemarketclone.dto.sign.SignUpRequest;
import com.example.kukyemarketclone.entity.member.Member;
import com.example.kukyemarketclone.entity.member.Role;
import com.example.kukyemarketclone.entity.member.RoleType;
import com.example.kukyemarketclone.exception.*;
import com.example.kukyemarketclone.factory.dto.SignInRequestFactory;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import com.example.kukyemarketclone.repository.role.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.example.kukyemarketclone.factory.dto.SignUpRequestFactory.createSignUpRequest;
import static com.example.kukyemarketclone.factory.entity.MemberFactory.createMember;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SignServiceTest {

    @InjectMocks SignService signService;
    @Mock
    MemberRepository memberRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    TokenService tokenService;

    @Test
    void signUpTest() {
        //given
        SignUpRequest req = createSignUpRequest();
        given(roleRepository.findByRoleType(RoleType.ROLE_NORMAL)).willReturn(Optional.of(new Role(RoleType.ROLE_NORMAL)));

        //when
        signService.signUp(req);

        //then
        verify(passwordEncoder).encode(req.getPassword());
        verify(memberRepository).save(any());
    }

    @Test
    void validateSignUpByDuplicateEmailTest(){
        //given : 이미 가입된 이메일
        given(memberRepository.existsByEmail(anyString())).willReturn(true);

        //when, then
        assertThatThrownBy(() -> signService.signUp(createSignUpRequest()))
                .isInstanceOf(MemberEmailAlreadyExistsException.class);
    }

    @Test
    void validateSignUpByDuplicateNicknameTest(){
        //given 이미 가입된 닉네임
        given(memberRepository.existsByNickname(anyString())).willReturn(true);

        //when,then
        assertThatThrownBy(() -> signService.signUp(createSignUpRequest()))
                .isInstanceOf(MemberNicknameAlreadyExistsException.class);

    }

    @Test
    void signUpRoleNotFoundTest(){
        //given
        given(roleRepository.findByRoleType(RoleType.ROLE_NORMAL)).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> signService.signUp(createSignUpRequest()))
                .isInstanceOf(RoleNotFoundException.class);
    }

    @Test
    void signInTest() {
        //given
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(createMember()));
        given(passwordEncoder.matches(anyString(),anyString())).willReturn(true);
        given(tokenService.createAccessToken(anyString())).willReturn("access");
        given(tokenService.createRefreshToken(anyString())).willReturn("refresh");

        //when
        SignInResponse res = signService.signIn(SignInRequestFactory.createSignInRequest("email","password"));

        //then
        assertThat(res.getAccessToken()).isEqualTo("access");
        assertThat(res.getRefreshToken()).isEqualTo("refresh");
    }

    @Test
    void signInExceptionByNoneMemberTest(){
        //given
        given(memberRepository.findByEmail(any())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> signService.signIn(SignInRequestFactory.createSignInRequest("email","password")))
                .isInstanceOf(LoginFailureException.class);
    }

    @Test
    void signInExceptionByInvalidPasswordTest(){
        //given
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(createMember()));
        given(passwordEncoder.matches(anyString(),anyString())).willReturn(false);

        //when, then
        assertThatThrownBy(() -> signService.signIn(SignInRequestFactory.createSignInRequest("email","password")))
                .isInstanceOf(LoginFailureException.class);
    }

}