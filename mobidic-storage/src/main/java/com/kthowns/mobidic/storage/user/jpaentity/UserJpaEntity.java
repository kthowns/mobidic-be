package com.kthowns.mobidic.storage.user.jpaentity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.model.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class UserJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "kakaoId", unique = true)
    private Long kakaoId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "password", nullable = false)
    @JsonIgnore
    private String password;

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

    @Column(name = "deactivated_at")
    private LocalDateTime deactivatedAt;

    public User toModel() {
        return User.builder()
                .id(this.id)
                .kakaoId(this.kakaoId)
                .email(this.email)
                .nickname(this.nickname)
                .password(this.password)
                .role(this.role)
                .isActive(this.isActive)
                .createdAt(this.createdAt)
                .deactivatedAt(this.deactivatedAt)
                .build();
    }

    public void update(String nickname, String password, UserRole role, Boolean isActive, LocalDateTime deactivatedAt, Long kakaoId) {
        this.nickname = nickname;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
        this.deactivatedAt = deactivatedAt;
        this.kakaoId = kakaoId;
    }
}
