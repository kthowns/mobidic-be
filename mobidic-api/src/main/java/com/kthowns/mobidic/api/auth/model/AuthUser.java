package com.kthowns.mobidic.api.auth.model;

import com.kthowns.mobidic.storage.user.jpaentity.UserJpaEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class AuthUser implements UserDetails {
    private UUID id;
    private String email;
    private String password;
    private Boolean isActive;
    private String role;

    @Override
    public String getUsername() {
        return email; // 이메일을 아이디로 사용
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public static AuthUser fromJpaEntity(UserJpaEntity userJpaEntity) {
        return AuthUser.builder()
                .id(userJpaEntity.getId())
                .email(userJpaEntity.getEmail())
                .password(userJpaEntity.getPassword())
                .isActive(userJpaEntity.isActive())
                .role(userJpaEntity.getRole().name())
                .build();
    }
}
