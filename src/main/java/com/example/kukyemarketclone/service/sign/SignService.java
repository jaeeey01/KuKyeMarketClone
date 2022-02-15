package com.example.kukyemarketclone.service.sign;

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
    private final TokenService tokenService; //토큰 발급

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
        String accessToken = tokenService.createAccessToken(subject);
        String refrashToken = tokenService.createRefreshToken(subject);
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
        String subject = tokenService.extractRefreshTokenSubject(rToken); //검증된 refreshToken에서 subject 추출
        String accessToken = tokenService.createAccessToken(subject);
        return new RefreshTokenResponse(accessToken);
    }

    private String createSubject(Member member){
        return String.valueOf(member.getId());
    }

    private void validateRefreshToken(String rToken){
        if(!tokenService.validateRefreshToken(rToken)){
            throw new AuthenticationEntryPointException();
        }
    }

}
