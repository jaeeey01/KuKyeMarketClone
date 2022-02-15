package com.example.kukyemarketclone.service.sign;

import com.example.kukyemarketclone.config.token.TokenHelper;
import com.example.kukyemarketclone.dto.sign.RefreshTokenResponse;
import com.example.kukyemarketclone.dto.sign.SignInRequest;
import com.example.kukyemarketclone.dto.sign.SignInResponse;
import com.example.kukyemarketclone.dto.sign.SignUpRequest;
import com.example.kukyemarketclone.entity.member.Member;

import com.example.kukyemarketclone.entity.member.RoleType;
import com.example.kukyemarketclone.exception.*;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import com.example.kukyemarketclone.repository.role.RoleRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignService {

    private final MemberRepository memberRepository; // 사용자 조회 및 등록 목적
    private final RoleRepository roleRepository;    //사용자 기본권한 부여 목적
    private final PasswordEncoder passwordEncoder; //비밀번호 암호화

    private final TokenHelper accessTokenHelper;    //주입 전략에 의해 타입이 동일한 여러 개의 빈에 대해서는,
    private final TokenHelper refreshTokenHelper;   //빈의 이름과 매핑되는 변수 명에 빈을 주입 받음(TokenConfig 메소드명 과 일치)

    @Transactional
    public void signUp(SignUpRequest req){
        validateSignUpInfo(req);
        memberRepository.save(SignUpRequest.toEntity(req,
                roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new),
                passwordEncoder));
    }
    @Transactional(readOnly = true)
    public SignInResponse signIn(SignInRequest req){
        Member member = memberRepository.findByEmail(req.getEmail()).orElseThrow(LoginFailureException::new);
        validatePassword(req,member);
        String subject = createSubject(member);
        String accessToken = accessTokenHelper.createToken(subject);
        String refrashToken = refreshTokenHelper.createToken(subject);
        return new SignInResponse(accessToken,refrashToken);
    }

    private void validateSignUpInfo(SignUpRequest req){
        if(memberRepository.existsByEmail(req.getEmail()))
            throw new MemberEmailAlreadyExistsException(req.getEmail());
        if(memberRepository.existsByNickname(req.getNickname()))
            throw new MemberNicknameAlreadyExistsException(req.getNickname());

    }

    private void validatePassword(SignInRequest req, Member member){
        if(!passwordEncoder.matches(req.getPassword(),member.getPassword())){
            throw new LoginFailureException();
        }
    }

    //refreshToken을 이용한 accessToken 재발급
    public RefreshTokenResponse refreshToken(String rToken){
        validateRefreshToken(rToken);
        String subject = refreshTokenHelper.extractSubject(rToken); //검증된 refreshToken에서 subject 추출
        String accessToken = accessTokenHelper.createToken(subject);
        return new RefreshTokenResponse(accessToken);
    }

    private String createSubject(Member member){
        return String.valueOf(member.getId());
    }

    private void validateRefreshToken(String rToken){
        if(!refreshTokenHelper.validate(rToken)){
            throw new AuthenticationEntryPointException();
        }
    }

}
