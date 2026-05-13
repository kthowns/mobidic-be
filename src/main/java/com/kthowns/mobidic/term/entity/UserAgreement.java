package com.kthowns.mobidic.term.entity;

import com.kthowns.mobidic.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "user_agreements",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "term_id"})
)
@EntityListeners(AuditingEntityListener.class)
public class UserAgreement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Term term;

    @Column(name = "agreed_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime agreedAt;
}
