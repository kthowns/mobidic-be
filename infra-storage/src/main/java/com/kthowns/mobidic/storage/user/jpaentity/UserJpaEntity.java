package com.kthowns.mobidic.storage.user.jpaentity;

import com.kthowns.mobidic.domain.global.model.AuditTime;
import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.model.UserRole;
import com.kthowns.mobidic.storage.global.jpaentity.BaseAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class UserJpaEntity extends BaseAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "kakao_id", unique = true)
    private Long kakaoId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "deactivated_at")
    private LocalDateTime deactivatedAt;

    public User toModel() {
        return new User(
                this.getId(),
                this.getKakaoId(),
                this.getEmail(),
                this.getNickname(),
                this.getPassword(),
                this.getRole(),
                this.active,
                AuditTime.of(getCreatedAt(), getUpdatedAt()),
                this.deactivatedAt
        );
    }

    public static UserJpaEntity createFromModel(User user) {
        return UserJpaEntity.builder()
                .kakaoId(user.kakaoId())
                .email(user.email())
                .nickname(user.nickname())
                .password(user.password())
                .build();
    }

    public void updateFromModel(User user) {
        this.email = user.email();
        this.nickname = user.nickname();
        this.password = user.password();
        this.role = user.role();
        this.deactivatedAt = user.deactivatedAt();
        this.active = user.isActive();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private UserJpaEntity(Long kakaoId, String email, String nickname, String password) {
        this.kakaoId = kakaoId;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = UserRole.USER;
        this.active = true;
    }
}
