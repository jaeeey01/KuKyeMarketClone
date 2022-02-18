package com.example.kukyemarketclone.service.member;

import com.example.kukyemarketclone.entity.member.Member;
import com.example.kukyemarketclone.exception.MemberNotFoundException;
import com.example.kukyemarketclone.repository.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.example.kukyemarketclone.factory.entity.MemberFactory.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @InjectMocks MemberService memberService;
    @Mock
    MemberRepository memberRepository;

    @Test
    void readTest(){
        //given
        Member member = createMember();
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        //when
        MemberDto result = memberService.read(1L);

        //then
        assertThat(result.getEmail()).isEqualTo(member.getEmail());

    }

    @Test
    void readExceptionByMemberNotFountTest(){
        //given
        given(memberRepository.findById(any())).willReturn(Optional.ofNullable(null));

        //when, then
        assertThatThrownBy(() -> memberService.read(1L)).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void deleteTest(){
        //given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));

        //when
        memberService.delete(1L);

        //then
        verify(memberRepository).delete(any());
    }

    @Test
    void deleteExceptionByMemberNotFoundTest(){
        //given
        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        //when, then
        assertThatThrownBy(()-> memberService.delete(1L)).isInstanceOf(MemberNotFoundException.class);
    }

}
