package com.kthowns.mobidic.storage.word.jpaentity;

import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.storage.vocabulary.jpaentity.VocabularyJpaEntity;
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
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "words")
@EntityListeners(AuditingEntityListener.class)
public class WordJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocabulary_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private VocabularyJpaEntity vocabulary;

    @Column(name = "expression", nullable = false)
    private String expression;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static WordJpaEntity fromModel(Word word, VocabularyJpaEntity vocabulary) {
        return new WordJpaEntity(
                word.id(),
                vocabulary,
                word.expression(),
                word.createdAt()
        );
    }

    public void updateFromModel(Word word) {
        this.expression = word.expression();
    }

    public Word toModel() {
        return new Word(
                this.id,
                this.vocabulary.getId(),
                this.expression,
                this.createdAt
        );
    }
}
