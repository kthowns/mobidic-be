package com.kthowns.mobidic.storage.definition.jpaentity;

import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.global.model.AuditTime;
import com.kthowns.mobidic.storage.global.jpaentity.BaseAuditingEntity;
import com.kthowns.mobidic.storage.word.jpaentity.WordJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "definitions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"word_id", "meaning"}))
public class DefinitionJpaEntity extends BaseAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private WordJpaEntity word;

    @Column(name = "meaning", nullable = false)
    private String meaning;

    @Column(name = "part", nullable = false)
    @Enumerated(EnumType.STRING)
    private PartOfSpeech part;

    public static DefinitionJpaEntity createFromModel(Definition definition, WordJpaEntity word) {
        return DefinitionJpaEntity.builder()
                .word(word)
                .meaning(definition.meaning())
                .part(definition.part())
                .build();
    }

    public void updateFromModel(Definition definition) {
        this.meaning = definition.meaning();
        this.part = definition.part();
    }

    public void updateFromModel(Definition definition, WordJpaEntity newWord) {
        updateFromModel(definition);

        this.word = newWord;
    }

    public Definition toModel() {
        return new Definition(
                this.getId(),
                this.word.getId(),
                this.meaning,
                this.part,
                AuditTime.of(this.getCreatedAt(), this.getUpdatedAt())
        );
    }

    @Builder(access = AccessLevel.PRIVATE)
    private DefinitionJpaEntity(
            WordJpaEntity word,
            String meaning,
            PartOfSpeech part
    ) {
        this.word = word;
        this.meaning = meaning;
        this.part = part;
    }
}
