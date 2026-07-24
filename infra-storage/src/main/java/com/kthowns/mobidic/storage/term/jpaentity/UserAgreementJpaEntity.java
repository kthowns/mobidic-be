package com.kthowns.mobidic.storage.term.jpaentity;

import com.kthowns.mobidic.storage.global.jpaentity.BaseAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "user_agreements",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "term_id"})
)
public class UserAgreementJpaEntity extends BaseAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TermJpaEntity term;

    public static UserAgreementJpaEntity create(UUID userId, TermJpaEntity term) {
        return UserAgreementJpaEntity.builder()
                .userId(userId)
                .term(term)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private UserAgreementJpaEntity(UUID userId, TermJpaEntity term) {
        this.userId = userId;
        this.term = term;
    }
}
