package com.kthowns.mobidic.storage.definition.jpaentity;

import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.storage.word.jpaentity.WordJpaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Data
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
}