package com.kthowns.mobidic.api.security.service;

import com.kthowns.mobidic.api.auth.model.AuthUser;
import com.kthowns.mobidic.common.code.AuthResponseCode;
import com.kthowns.mobidic.domain.auth.repository.AuthUserRepository;
import com.kthowns.mobidic.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AuthUserRepository authUserRepository;

    @Override
    @Transactional(readOnly = true)
    public AuthUser loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = authUserRepository.readByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(AuthResponseCode.NO_USER.getMessage()));

        AuthUser authUser = AuthUser.fromUser(user);

        if (!authUser.getIsActive()) {
            throw new UsernameNotFoundException(AuthResponseCode.NO_USER.getMessage());
        }

        return authUser;
    }
}
