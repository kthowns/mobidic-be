package com.kthowns.mobidic.api.dictionary.entity;

import com.kthowns.mobidic.api.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "vocabularies")
@EntityListeners(AuditingEntityListener.class)
public class Vocabulary {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "BINARY(16)")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Setter
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "word_count", nullable = false)
    @Builder.Default
    private Long wordCount = 0L;

    @Setter
    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    public void addWordCount() {
        wordCount++;
    }

    public void removeWordCount() {
        wordCount--;
    }
}