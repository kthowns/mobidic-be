package com.kthowns.mobidic.api.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kthowns.mobidic.api.user.type.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "kakaoId", unique = true)
    private Long kakaoId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Setter
    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Setter
    @Column(name = "password", nullable = false)
    @JsonIgnore
    private String password;

    @Setter
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Setter
    @Column(name = "deactivated_at")
    private LocalDateTime deactivatedAt;

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
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
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

}