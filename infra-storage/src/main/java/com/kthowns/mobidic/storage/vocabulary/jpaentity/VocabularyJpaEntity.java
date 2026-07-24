package com.kthowns.mobidic.storage.vocabulary.jpaentity;

import com.kthowns.mobidic.domain.global.model.AuditTime;
import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.storage.global.jpaentity.BaseAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "vocabularies")
public class VocabularyJpaEntity extends BaseAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "word_count", nullable = false)
    private long wordCount;

    @Column(name = "description")
    private String description;

    public static VocabularyJpaEntity createFromModel(Vocabulary vocabulary) {
        return VocabularyJpaEntity.builder()
                .userId(vocabulary.userId())
                .title(vocabulary.title())
                .description(vocabulary.description())
                .wordCount(vocabulary.wordCount())
                .build();
    }

    public void updateFromModel(Vocabulary vocabulary) {
        this.userId = vocabulary.userId();
        this.title = vocabulary.title();
        this.description = vocabulary.description();
        this.wordCount = vocabulary.wordCount();
    }

    public Vocabulary toModel() {
        return new Vocabulary(
                this.id,
                this.userId,
                this.title,
                this.description,
                this.wordCount,
                AuditTime.of(getCreatedAt(), getUpdatedAt())
        );
    }

    @Builder(access = AccessLevel.PRIVATE)
    private VocabularyJpaEntity(UUID userId, String title, String description, long wordCount) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.wordCount = wordCount;
    }
}
