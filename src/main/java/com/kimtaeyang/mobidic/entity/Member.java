package com.kimtaeyang.mobidic.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="member")
public class Member implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id", columnDefinition = "BINARY(16)")
    private UUID id;
    @Column(name="email")
    private String email;
    @Column(name="nickname")
    private String nickname;
    @Column(name="password")
    @JsonIgnore
    private String password;
    @Column(name="is_active", insertable=false)
    private Boolean isActive;
    @Column(name="created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
    @Column(name="withdrawn_at")
    private Timestamp withdrawnAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return email; // 이메일을 아이디로 사용
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

    @Override
    public boolean isEnabled() {
        return true;
    }
}