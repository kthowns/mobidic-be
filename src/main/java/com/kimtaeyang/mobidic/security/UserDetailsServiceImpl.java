package com.kimtaeyang.mobidic.security;

import com.kimtaeyang.mobidic.entity.Member;
import com.kimtaeyang.mobidic.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.kimtaeyang.mobidic.code.AuthResponseCode.NO_MEMBER;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl  implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(NO_MEMBER.getMessage()));

        if(!member.getIsActive()){
            throw new UsernameNotFoundException(NO_MEMBER.getMessage());
        }

        return member;
    }
}
