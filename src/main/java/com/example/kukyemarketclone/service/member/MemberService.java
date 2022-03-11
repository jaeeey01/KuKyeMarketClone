package com.example.kukyemarketclone.service.member;

import com.example.kukyemarketclone.dto.member.MemberDto;
import com.example.kukyemarketclone.entity.member.Member;
import com.example.kukyemarketclone.exception.MemberNotFoundException;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly=true)
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberDto read(Long id){
        return MemberDto.toDto(memberRepository.findById(id).orElseThrow(MemberNotFoundException::new));
    }

    @Transactional
    @PreAuthorize("@memberGuard.check(#id)")
    public void delete(Long id){
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        memberRepository.delete(member);
    }

}
