package com.kimtaeyang.mobidic.security.service;

import com.kimtaeyang.mobidic.user.entity.User;
import com.kimtaeyang.mobidic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.kimtaeyang.mobidic.common.code.AuthResponseCode.NO_MEMBER;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(NO_MEMBER.getMessage()));

        if (!user.getIsActive()) {
            throw new UsernameNotFoundException(NO_MEMBER.getMessage());
        }

        return user;
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(NO_MEMBER.getMessage()));

        if (!user.getIsActive()) {
            throw new UsernameNotFoundException(NO_MEMBER.getMessage());
        }

        return user;
    }
}
