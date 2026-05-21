package com.kthowns.mobidic.storage.vocabulary.jpaentity;

import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.storage.user.jpaentity.UserJpaEntity;
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
public class VocabularyJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "BINARY(16)")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserJpaEntity user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "word_count", nullable = false)
    @Builder.Default
    private Long wordCount = 0L;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    public void update(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void syncWordCount(Long wordCount) {
        this.wordCount = wordCount;
    }

    public void addWordCount() {
        wordCount++;
    }

    public void removeWordCount() {
        wordCount--;
    }

    public Vocabulary toModel() {
        return Vocabulary.builder()
                .id(this.id)
                .userId(this.user.getId())
                .title(this.title)
                .description(this.description)
                .wordCount(this.wordCount)
                .createdAt(this.createdAt)
                .build();
    }
}
