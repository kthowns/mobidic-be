package com.kthowns.mobidic.storage.user.jpaentity;

import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.model.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private String password;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

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
        return new User(
                this.getId(),
                this.getKakaoId(),
                this.getEmail(),
                this.getNickname(),
                this.getPassword(),
                this.getRole(),
                this.isActive,
                this.createdAt,
                this.deactivatedAt
        );
    }

    public static UserJpaEntity createFromModel(User user) {
        return new UserJpaEntity(
                user.id(),
                user.kakaoId(),
                user.email(),
                user.nickname(),
                user.password(),
                user.isActive(),
                user.role(),
                user.createdAt(),
                user.deactivatedAt()
        );
    }

    public void updateFromModel(User user) {
        this.nickname = user.nickname();
        this.password = user.password();
        this.role = user.role();
        this.isActive = user.isActive();
        this.deactivatedAt = user.deactivatedAt();
    }
}
