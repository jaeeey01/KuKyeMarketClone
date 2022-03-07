package com.example.kukyemarketclone.service.sign;

import com.example.kukyemarketclone.config.token.TokenHelper;
import com.example.kukyemarketclone.dto.sign.RefreshTokenResponse;
import com.example.kukyemarketclone.dto.sign.SignInResponse;
import com.example.kukyemarketclone.dto.sign.SignUpRequest;
import com.example.kukyemarketclone.entity.member.Role;
import com.example.kukyemarketclone.entity.member.RoleType;
import com.example.kukyemarketclone.exception.*;
import com.example.kukyemarketclone.factory.dto.SignInRequestFactory;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import com.example.kukyemarketclone.repository.role.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static com.example.kukyemarketclone.factory.dto.SignInRequestFactory.createSignInRequest;
import static com.example.kukyemarketclone.factory.dto.SignUpRequestFactory.createSignUpRequest;
import static com.example.kukyemarketclone.factory.entity.MemberFactory.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SignServiceTest {

    SignService signService;
    @Mock
    MemberRepository memberRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    TokenHelper accessTokenHelper;
    @Mock
    TokenHelper refreshTokenHelper;

    @BeforeEach
    void beforeEach(){ // 동일한 타입의 @Mock에 대해서 Mockito가 제대로 인식을 못하기 때문에 의존성 직접 지정
        signService = new SignService(memberRepository,roleRepository,passwordEncoder,accessTokenHelper,refreshTokenHelper);
    }

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
        given(memberRepository.findWithRolesByEmail(any())).willReturn(Optional.of(createMember()));
        given(passwordEncoder.matches(anyString(),anyString())).willReturn(true);
        given(accessTokenHelper.createToken(any())).willReturn("access");
        given(refreshTokenHelper.createToken(any())).willReturn("refresh");

        //when
        SignInResponse res = signService.signIn(createSignInRequest("email","password"));

        //then
        assertThat(res.getAccessToken()).isEqualTo("access");
        assertThat(res.getRefreshToken()).isEqualTo("refresh");
    }

    @Test
    void signInExceptionByNoneMemberTest(){
        //given
        given(memberRepository.findWithRolesByEmail(any())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> signService.signIn(createSignInRequest("email","password")))
                .isInstanceOf(LoginFailureException.class);
    }

    @Test
    void signInExceptionByInvalidPasswordTest(){
        //given
        given(memberRepository.findWithRolesByEmail(any())).willReturn(Optional.of(createMember()));
        given(passwordEncoder.matches(anyString(),anyString())).willReturn(false);

        //when, then
        assertThatThrownBy(() -> signService.signIn(createSignInRequest("email","password")))
                .isInstanceOf(LoginFailureException.class);
    }

    @Test
    void refreshTokenTest(){
        //given
        String refreshToken = "refreshToken";
        String subject = "subject";
        String accessToken = "accessToken";
        given(refreshTokenHelper.parse(refreshToken)).willReturn(Optional.of(new TokenHelper.PrivateClaims("memberId", List.of("ROLE_NORMAL"))));
        given(accessTokenHelper.createToken(any())).willReturn(accessToken);
        //when
        RefreshTokenResponse res = signService.refreshToken(refreshToken);

        //then
        assertThat(res.getAccessToken()).isEqualTo(accessToken);
    }

    @Test
    void refreshTokenExceptionByInvalidTokenTest(){
        //given
        String refreshToken = "refreshToken";
        given(refreshTokenHelper.parse(refreshToken)).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> signService.refreshToken(refreshToken))
                .isInstanceOf(RefreshTokenFailureException.class);
    }

}