package com.kthowns.mobidic.storage.definition.jpaentity;

import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.storage.word.jpaentity.WordJpaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "definitions")
public class DefinitionJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
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

    public void update(String meaning, PartOfSpeech part) {
        this.meaning = meaning;
        this.part = part;
    }

    public static DefinitionJpaEntity createFromModel(Definition definition, WordJpaEntity word) {
        return new DefinitionJpaEntity(
                definition.id(),
                word,
                definition.meaning(),
                definition.part()
        );
    }

    public void updateFromModel(Definition definition) {
        this.meaning = definition.meaning();
        this.part = definition.part();
    }

    public Definition toModel() {
        return new Definition(
                this.id,
                this.word.getId(),
                this.meaning,
                this.part
        );
    }
}
