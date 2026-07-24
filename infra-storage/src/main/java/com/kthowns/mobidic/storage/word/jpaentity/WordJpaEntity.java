package com.kthowns.mobidic.storage.word.jpaentity;

import com.kthowns.mobidic.domain.global.model.AuditTime;
import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.storage.global.jpaentity.BaseAuditingEntity;
import com.kthowns.mobidic.storage.vocabulary.jpaentity.VocabularyJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "words")
public class WordJpaEntity extends BaseAuditingEntity {
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

    public static WordJpaEntity createFromModel(Word word, VocabularyJpaEntity vocabulary) {
        return WordJpaEntity.builder()
                .vocabulary(vocabulary)
                .expression(word.expression())
                .build();
    }

    public void updateFromModel(Word word) {
        this.expression = word.expression();
    }

    public Word toModel() {
        return new Word(
                this.id,
                this.vocabulary.getId(),
                this.expression,
                AuditTime.create()
        );
    }

    @Builder(access = AccessLevel.PRIVATE)
    private WordJpaEntity(VocabularyJpaEntity vocabulary, String expression) {
        this.vocabulary = vocabulary;
        this.expression = expression;
    }
}
